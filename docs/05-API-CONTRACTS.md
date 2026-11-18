# Dentooth — API Contracts & Backend Schema

## 1. Supabase Project Setup

### 1.1 Required Services
- **Auth**: Email/password authentication
- **Database**: PostgreSQL (all tables from Data Models doc)
- **Storage**: Buckets for images, X-rays, PDFs, avatars
- **Realtime**: Subscriptions for appointment updates, medical history sync
- **Edge Functions**: SMS reminders, QR form handler, PDF generation

### 1.2 Storage Buckets

| Bucket | Purpose | Max Size | Allowed Types | Access |
|--------|---------|----------|---------------|--------|
| `avatars` | Staff & patient photos | 2MB | image/jpeg, image/png | Authenticated read; owner write |
| `patient-images` | X-rays, intraoral photos | 10MB | image/jpeg, image/png, image/dicom | Clinic members read; doctors write |
| `invoices` | Generated invoice PDFs | 5MB | application/pdf | Clinic members read; system write |
| `receipts` | Expense receipt photos | 5MB | image/jpeg, image/png | Admin + accountant read/write |
| `clinic-assets` | Logos, branding | 2MB | image/jpeg, image/png, image/svg+xml | Admin write; all read |

---

## 2. Row Level Security (RLS) Policies

### 2.1 Core Pattern: Clinic Isolation

Every table with `clinic_id` uses this base pattern:

```sql
-- Users can only see data from their own clinic
CREATE POLICY "clinic_isolation" ON <table>
  USING (clinic_id = (SELECT clinic_id FROM user_account WHERE id = auth.uid()));

-- Users can only insert data for their own clinic
CREATE POLICY "clinic_insert" ON <table>
  FOR INSERT
  WITH CHECK (clinic_id = (SELECT clinic_id FROM user_account WHERE id = auth.uid()));
```

### 2.2 Role-Based Policies

```sql
-- Helper function: get current user's role
CREATE OR REPLACE FUNCTION get_user_role()
RETURNS TEXT AS $$
  SELECT role FROM user_account WHERE id = auth.uid();
$$ LANGUAGE sql SECURITY DEFINER;

-- Helper function: get current user's clinic_id
CREATE OR REPLACE FUNCTION get_user_clinic_id()
RETURNS UUID AS $$
  SELECT clinic_id FROM user_account WHERE id = auth.uid();
$$ LANGUAGE sql SECURITY DEFINER;

-- Example: Only admin can delete patients
CREATE POLICY "admin_delete_patient" ON patient
  FOR DELETE
  USING (
    clinic_id = get_user_clinic_id()
    AND get_user_role() = 'admin'
  );

-- Example: Accountants cannot view dental charts
CREATE POLICY "no_chart_for_accountant" ON dental_chart
  USING (
    get_user_role() != 'accountant'
    AND patient_id IN (SELECT id FROM patient WHERE clinic_id = get_user_clinic_id())
  );
```

### 2.3 Key Policies by Table

| Table | SELECT | INSERT | UPDATE | DELETE |
|-------|--------|--------|--------|--------|
| patient | All roles (clinic-scoped) | Admin, Doctor, Receptionist | Admin, Doctor, Receptionist | Admin only |
| medical_history | All except Accountant | Doctor, Receptionist | Doctor, Receptionist | Admin only |
| dental_chart | All except Accountant | Doctor | Doctor | Admin only |
| tooth_record | All except Accountant | Doctor | Doctor | Admin only |
| treatment_plan | All (clinic-scoped) | Doctor | Doctor | Admin, Doctor (own) |
| appointment | All except Accountant | Admin, Doctor, Receptionist | Admin, Doctor, Receptionist | Admin, Receptionist |
| invoice | All (clinic-scoped) | Admin, Receptionist, Accountant | Admin, Receptionist, Accountant | Admin only |
| payment | All (clinic-scoped) | Admin, Receptionist, Accountant | Admin only | Admin only |
| expense | Admin, Accountant | Admin, Accountant | Admin, Accountant | Admin only |
| staff_profile | All (clinic-scoped) | Admin only | Admin (all), self (own) | Admin only |
| inventory_item | Admin, Doctor, Accountant | Admin, Accountant | Admin, Accountant | Admin only |
| branch | All (clinic-scoped) | Admin only | Admin only | Admin only |

---

## 3. Database Functions & Triggers

### 3.1 Auto-Update Timestamps
```sql
CREATE OR REPLACE FUNCTION update_updated_at()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = now();
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Apply to all tables with updated_at
CREATE TRIGGER set_updated_at
  BEFORE UPDATE ON patient
  FOR EACH ROW EXECUTE FUNCTION update_updated_at();
-- ... repeat for other tables
```

### 3.2 Auto-Generate Patient Number
```sql
CREATE OR REPLACE FUNCTION generate_patient_number()
RETURNS TRIGGER AS $$
DECLARE
  next_num INTEGER;
BEGIN
  SELECT COALESCE(MAX(CAST(SUBSTRING(patient_number FROM 3) AS INTEGER)), 0) + 1
  INTO next_num
  FROM patient
  WHERE clinic_id = NEW.clinic_id;

  NEW.patient_number = 'P-' || LPAD(next_num::TEXT, 5, '0');
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER auto_patient_number
  BEFORE INSERT ON patient
  FOR EACH ROW
  WHEN (NEW.patient_number IS NULL)
  EXECUTE FUNCTION generate_patient_number();
```

### 3.3 Auto-Generate Invoice Number
```sql
CREATE OR REPLACE FUNCTION generate_invoice_number()
RETURNS TRIGGER AS $$
DECLARE
  next_num INTEGER;
  year_str TEXT;
BEGIN
  year_str = EXTRACT(YEAR FROM NOW())::TEXT;
  SELECT COALESCE(MAX(CAST(SUBSTRING(invoice_number FROM 10) AS INTEGER)), 0) + 1
  INTO next_num
  FROM invoice
  WHERE clinic_id = NEW.clinic_id
  AND invoice_number LIKE 'INV-' || year_str || '-%';

  NEW.invoice_number = 'INV-' || year_str || '-' || LPAD(next_num::TEXT, 5, '0');
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;
```

### 3.4 Update Invoice Balance on Payment
```sql
CREATE OR REPLACE FUNCTION update_invoice_on_payment()
RETURNS TRIGGER AS $$
DECLARE
  total_paid DECIMAL(10,2);
  inv_total DECIMAL(10,2);
BEGIN
  SELECT SUM(amount) INTO total_paid FROM payment WHERE invoice_id = NEW.invoice_id;
  SELECT total INTO inv_total FROM invoice WHERE id = NEW.invoice_id;

  UPDATE invoice SET
    amount_paid = total_paid,
    status = CASE
      WHEN total_paid >= inv_total THEN 'paid'
      WHEN total_paid > 0 THEN 'partial'
      ELSE status
    END
  WHERE id = NEW.invoice_id;

  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER payment_update_invoice
  AFTER INSERT ON payment
  FOR EACH ROW EXECUTE FUNCTION update_invoice_on_payment();
```

### 3.5 Update Treatment Plan Total on Procedure Change
```sql
CREATE OR REPLACE FUNCTION update_plan_total()
RETURNS TRIGGER AS $$
BEGIN
  UPDATE treatment_plan SET
    total_cost = (SELECT COALESCE(SUM(cost * quantity), 0) FROM plan_procedure WHERE plan_id = COALESCE(NEW.plan_id, OLD.plan_id))
  WHERE id = COALESCE(NEW.plan_id, OLD.plan_id);
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER procedure_update_plan_total
  AFTER INSERT OR UPDATE OR DELETE ON plan_procedure
  FOR EACH ROW EXECUTE FUNCTION update_plan_total();
```

---

## 4. Supabase Client Queries (Kotlin SDK)

### 4.1 Client Setup
```kotlin
val supabaseClient = createSupabaseClient(
    supabaseUrl = BuildConfig.SUPABASE_URL,
    supabaseKey = BuildConfig.SUPABASE_ANON_KEY
) {
    install(Auth)
    install(Postgrest)
    install(Storage)
    install(Realtime)
}
```

### 4.2 Common Query Patterns

```kotlin
// --- Auth ---
suspend fun signIn(email: String, password: String) =
    supabaseClient.auth.signInWith(Email) {
        this.email = email
        this.password = password
    }

suspend fun signOut() = supabaseClient.auth.signOut()
fun currentUser() = supabaseClient.auth.currentUserOrNull()

// --- Patients ---
suspend fun getPatients(clinicId: String, search: String? = null): List<PatientDto> =
    supabaseClient.from("patient")
        .select {
            filter { eq("clinic_id", clinicId) }
            if (search != null) {
                filter { or {
                    ilike("first_name", "%$search%")
                    ilike("last_name", "%$search%")
                    ilike("phone", "%$search%")
                    ilike("patient_number", "%$search%")
                }}
            }
            order("created_at", Order.DESCENDING)
        }
        .decodeList()

suspend fun getPatient(id: String): PatientDto =
    supabaseClient.from("patient")
        .select { filter { eq("id", id) } }
        .decodeSingle()

suspend fun createPatient(patient: PatientDto): PatientDto =
    supabaseClient.from("patient")
        .insert(patient) { select() }
        .decodeSingle()

suspend fun updatePatient(id: String, updates: PatientDto) =
    supabaseClient.from("patient")
        .update(updates) { filter { eq("id", id) } }

suspend fun deletePatient(id: String) =
    supabaseClient.from("patient")
        .delete { filter { eq("id", id) } }

// --- Appointments (with joins) ---
suspend fun getAppointments(branchId: String, date: LocalDate): List<AppointmentWithDetails> =
    supabaseClient.from("appointment")
        .select(columns = Columns.raw("""
            *,
            patient:patient_id(id, first_name, last_name, phone, photo_url),
            doctor:doctor_id(id, user:user_id(full_name, avatar_url), color),
            chair:chair_id(id, name)
        """)) {
            filter {
                eq("branch_id", branchId)
                gte("start_time", "${date}T00:00:00")
                lt("start_time", "${date.plus(1, DateTimeUnit.DAY)}T00:00:00")
            }
            order("start_time", Order.ASCENDING)
        }
        .decodeList()

// --- Dental Chart ---
suspend fun getDentalChart(patientId: String): DentalChartWithTeeth =
    supabaseClient.from("dental_chart")
        .select(columns = Columns.raw("""
            *,
            teeth:tooth_record(
                *,
                conditions:tooth_condition(*),
                procedures:tooth_procedure(*)
            )
        """)) {
            filter { eq("patient_id", patientId) }
        }
        .decodeSingle()

// --- Invoices ---
suspend fun getInvoices(clinicId: String, status: String? = null): List<InvoiceWithPatient> =
    supabaseClient.from("invoice")
        .select(columns = Columns.raw("""
            *,
            patient:patient_id(id, first_name, last_name, patient_number),
            items:invoice_item(*),
            payments:payment(*)
        """)) {
            filter {
                eq("clinic_id", clinicId)
                if (status != null) eq("status", status)
            }
            order("issued_at", Order.DESCENDING)
        }
        .decodeList()

// --- Realtime Subscription ---
fun subscribeToAppointments(branchId: String, onUpdate: (RealtimeMessage) -> Unit) {
    val channel = supabaseClient.realtime.channel("appointments-$branchId")
    channel.postgresChangeFlow<PostgresAction>(schema = "public") {
        table = "appointment"
        filter = "branch_id=eq.$branchId"
    }.onEach { onUpdate(it) }.launchIn(coroutineScope)
    channel.subscribe()
}
```

---

## 5. Edge Functions

### 5.1 SMS Reminder
```
POST /functions/v1/send-reminder
Body: { appointmentId: string, channel: "sms" | "whatsapp" }
Response: { success: boolean, messageId?: string, error?: string }
```

### 5.2 QR Medical History Form
```
POST /functions/v1/generate-qr-token
Body: { patientId: string }
Response: { token: string, url: string, expiresAt: string }

GET /functions/v1/medical-form?token=xxx
Response: HTML medical history form

POST /functions/v1/submit-medical-form
Body: { token: string, ...medicalHistoryFields }
Response: { success: boolean }
```

### 5.3 Invoice PDF Generation
```
POST /functions/v1/generate-invoice-pdf
Body: { invoiceId: string }
Response: { pdfUrl: string }
```

### 5.4 Birthday Greeting
```
POST /functions/v1/send-birthday-greetings
(Triggered by cron, no body needed)
Response: { sent: number, failed: number }
```

---

## 6. Offline Sync Strategy

### 6.1 Architecture
```
App UI ←→ Repository ←→ SQLDelight (Local) ←→ SyncEngine ←→ Supabase (Remote)
```

### 6.2 Sync Rules
1. **Reads**: Always from local SQLDelight first
2. **Writes**: Write to local → enqueue sync → sync when online
3. **Conflict resolution**: Last-write-wins using `updated_at` timestamp
4. **Sync queue**: Table `sync_queue` tracks pending changes
5. **Background sync**: On app foreground + periodic (every 5 min when online)

### 6.3 Local Sync Queue Table
```sql
CREATE TABLE sync_queue (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    table_name TEXT NOT NULL,
    record_id TEXT NOT NULL,
    operation TEXT NOT NULL, -- 'INSERT', 'UPDATE', 'DELETE'
    payload TEXT NOT NULL,   -- JSON of the record
    created_at TEXT NOT NULL,
    synced_at TEXT,
    retry_count INTEGER DEFAULT 0,
    error TEXT
);
```
