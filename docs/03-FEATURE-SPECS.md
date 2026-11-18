# Dentooth — Feature Specifications

## 1. Authentication & Access Control

### 1.1 Login Screen
- **Email + password** login via Supabase Auth
- "Remember me" toggle (persists token locally)
- "Forgot password" → Supabase password reset email
- After login → route based on role:
  - Admin/Doctor → Dashboard
  - Receptionist → Calendar (today view)
  - Accountant → Billing list

### 1.2 Registration
- Admin-only: invite staff via email
- Registration flow: Email → Set password → Choose role → Assign branch
- No self-registration (clinic management, not patient-facing)

### 1.3 Role-Based Access Control (RBAC)

| Feature | Admin | Doctor | Receptionist | Accountant | Assistant |
|---------|-------|--------|-------------|------------|-----------|
| Dashboard | ✅ Full | ✅ Own stats | ✅ Appointments | ✅ Financial | ❌ |
| Patients — View | ✅ | ✅ | ✅ | ✅ | ✅ Assigned |
| Patients — Create/Edit | ✅ | ✅ | ✅ | ❌ | ❌ |
| Patients — Delete | ✅ | ❌ | ❌ | ❌ | ❌ |
| Medical History | ✅ | ✅ | ✅ Read | ❌ | ✅ Read |
| Dental Chart | ✅ | ✅ | ✅ Read | ❌ | ✅ Read |
| Treatment Plans | ✅ | ✅ Create/Edit | ✅ Read | ✅ Read | ✅ Read |
| Appointments — View | ✅ All | ✅ Own | ✅ All | ❌ | ✅ Own doctor |
| Appointments — Create/Edit | ✅ | ✅ | ✅ | ❌ | ❌ |
| Invoices — View | ✅ | ✅ Own | ✅ | ✅ | ❌ |
| Invoices — Create/Edit | ✅ | ✅ | ✅ | ✅ | ❌ |
| Payments — Record | ✅ | ❌ | ✅ | ✅ | ❌ |
| Expenses | ✅ | ❌ | ❌ | ✅ | ❌ |
| Insurance / Claims | ✅ | ✅ Read | ✅ | ✅ | ❌ |
| Inventory | ✅ | ✅ Read | ❌ | ✅ | ❌ |
| Staff Management | ✅ | ❌ | ❌ | ❌ | ❌ |
| Branches | ✅ | ❌ | ❌ | ❌ | ❌ |
| Settings | ✅ | ✅ Own profile | ✅ Own profile | ✅ Own profile | ✅ Own profile |
| Analytics | ✅ Full | ✅ Own | ❌ | ✅ Financial | ❌ |

---

## 2. Patient Module

### 2.1 Patient List Screen
- **Search**: Real-time search by name, phone, patient number
- **Filters**: Active/inactive, patient group, date range (created)
- **Sort**: Name A-Z, Last visit (recent first), Created date
- **Quick actions**: Call, WhatsApp, New appointment
- **Bulk actions** (admin): Delete, assign group, export
- **FAB**: + Add new patient

### 2.2 Patient Detail Screen
- **Header**: Photo (tap to change), name, patient number, phone (tappable), age, gender
- **Tabs**:
  1. **Overview**: Contact info, groups, notes, next appointment, balance due
  2. **Medical History**: Conditions checklist, allergies, medications (editable form)
  3. **Dental Chart**: Interactive tooth diagram (see §3)
  4. **Treatments**: List of treatment plans with status badges
  5. **Appointments**: Appointment history with statuses
  6. **Invoices**: Financial summary: total billed, paid, balance; invoice list
  7. **Images**: Gallery of X-rays and photos with type filters
  8. **Files/Notes**: Clinical notes timeline

### 2.3 Patient Create/Edit Form
- **Required**: First name, last name, phone
- **Optional**: All other fields
- **Validation**: Phone format, email format, DOB not in future
- **Auto-generate**: Patient number on create

### 2.4 Medical History Form
- **Systemic conditions**: Checkbox grid (diabetes, hypertension, heart disease, etc.)
- **Allergies**: Free-text with tag-style chips
- **Medications**: Free-text
- **Pregnancy/breastfeeding**: Toggle (shown for female patients)
- **Smoking**: Toggle
- **Previous surgeries**: Free-text
- **QR Code Generation** (P1): Generate QR → patient scans → opens web form → submits → data syncs to app
- **Last updated**: Show timestamp and who updated

### 2.5 Patient Groups
- Create/edit groups with name and color
- Assign patients to multiple groups
- Filter patient list by group
- Use cases: VIP, Insurance-X patients, Ortho patients, Referral-Dr-Y

---

## 3. Dental Chart Module

### 3.1 Chart Canvas
- **Visual diagram**: Custom Compose Canvas drawing of full adult dentition (32 teeth) or pediatric (20 teeth)
- **Layout**: Upper arch (right to left: 18-11, 21-28) and lower arch (right to left: 48-41, 31-38) — FDI notation
- **Each tooth shows**:
  - Outline shape (molar/premolar/canine/incisor)
  - 5 surfaces color-coded by condition (healthy=white, caries=red, filled=blue, crown=gold, etc.)
  - Status icon overlay (missing=X, implant=screw icon, root canal=dot, etc.)
  - Selection highlight when tapped
- **Interaction**: Tap tooth → opens Tooth Detail Bottom Sheet
- **Legend**: Color/icon legend bar at bottom
- **Toggle**: Adult/Pediatric switch

### 3.2 Tooth Detail Bottom Sheet
When a tooth is tapped:
- **Header**: Tooth number, name (e.g., "Upper Right First Molar"), current status
- **Surfaces grid**: 5 clickable surfaces (M, D, B, L, O/I) — tap to select for condition/procedure
- **Active conditions**: List with severity badges, diagnosed date
- **Planned procedures**: From treatment plan, with status
- **Completed procedures**: History
- **Images**: X-rays/photos attached to this tooth
- **Actions**:
  - "Add Condition" → Condition picker (caries, fracture, etc.) + surface selector
  - "Plan Procedure" → Procedure picker + add to treatment plan
  - "Add Image" → Camera/gallery → tagged to this tooth
  - "Mark as Missing/Extracted"

### 3.3 Color Coding System

| Status | Fill Color | Outline |
|--------|-----------|---------|
| Healthy | White `#FFFFFF` | Gray `#9E9E9E` |
| Caries | Red `#EF5350` | Dark Red `#C62828` |
| Filled / Restored | Blue `#42A5F5` | Dark Blue `#1565C0` |
| Crown | Gold `#FFB300` | Dark Gold `#E65100` |
| Root Canal | Purple `#AB47BC` | Dark Purple `#6A1B9A` |
| Missing | Light Gray `#E0E0E0` | Dashed Gray |
| Implant | Teal `#26A69A` | Dark Teal `#00695C` |
| Bridge | Orange `#FFA726` | Dark Orange `#E65100` |
| Veneer | Light Blue `#81D4FA` | Blue `#0277BD` |
| Planned procedure | Yellow outline/dashed | — |

---

## 4. Treatment Planning Module

### 4.1 Treatment Plan List
- Per patient: list of all plans with status, doctor, total cost, date
- Status badges: Planned (gray), Active (blue), Completed (green), Cancelled (red)
- Filter by status

### 4.2 Treatment Plan Detail
- **Header**: Title, doctor, status, dates
- **Procedure list**: Ordered list of procedures with:
  - Procedure name, tooth number, surfaces
  - Cost, status (planned/in-progress/completed)
  - Swipe to complete, edit, or cancel
- **Summary**: Total cost, discount, final amount, amount invoiced, balance
- **Actions**:
  - Add procedure (from catalog or custom)
  - Reorder procedures (drag & drop)
  - Change status
  - Generate invoice from plan
  - Print/share plan PDF

### 4.3 Procedure Catalog
- Pre-seeded with common dental procedures by category:
  - **Diagnostic**: Exam, X-ray, Panoramic
  - **Preventive**: Cleaning, Fluoride, Sealant
  - **Restorative**: Filling (composite/amalgam), Inlay, Onlay
  - **Endodontic**: Root Canal (anterior/premolar/molar), Retreatment
  - **Prosthodontic**: Crown (PFM/Zirconia/E-max), Bridge, Veneer, Denture
  - **Surgical**: Extraction (simple/surgical), Implant, Bone Graft
  - **Periodontic**: Scaling, Root Planing, Gum Surgery
  - **Orthodontic**: Braces, Clear Aligners, Retainer
- Customizable: Clinics can add/edit/disable procedures and prices

---

## 5. Calendar & Scheduling Module

### 5.1 Calendar Views
- **Day view**: Timeline (7 AM – 10 PM), 30-min slots, appointments as blocks
  - Color coded by: doctor, status, or type (configurable)
  - Shows patient name, procedure, duration
  - Columns: by chair or by doctor (toggle)
- **Week view**: 7-day timeline, condensed appointment blocks
- **Month view**: Date grid with appointment count dots; tap date → day view
- **Agenda view**: Flat list of upcoming appointments

### 5.2 Appointment Booking
- **Quick book**: From calendar → tap empty slot → auto-fills date/time
- **Full form**: Patient (search/select), Doctor, Branch, Chair, Date, Start time, End time (auto from duration), Type, Notes
- **Conflict detection**: Warn if doctor or chair double-booked
- **From patient**: Patient detail → "Book Appointment" → patient pre-filled
- **From treatment plan**: Link appointment to specific procedure

### 5.3 Appointment Detail
- Patient info summary
- Status buttons: Check-in, Start, Complete, Cancel, No-show
- Link to dental chart, treatment plan
- Notes field
- Reminder status (sent/pending)

### 5.4 Reminders (P1)
- Auto-send SMS reminder 24 hours before appointment
- Configurable: 48h, 24h, 2h, or custom
- Template customizable per clinic
- Support Arabic/English templates
- WhatsApp integration (P2): via WhatsApp Business API

---

## 6. Billing & Finance Module

### 6.1 Invoice List
- Filter: Status (all/draft/sent/paid/partial/overdue), date range, patient
- Sort: Date, amount, status
- Summary cards: Total billed, Total collected, Outstanding balance
- Color-coded status badges

### 6.2 Invoice Builder
- **Auto-generate**: From treatment plan → pulls completed procedures as line items
- **Manual**: Add custom line items
- **Line item**: Description, quantity, unit price, total
- **Calculations**: Subtotal, discount (% or fixed), tax (%), total
- **Patient info**: Auto-filled from patient record
- **Clinic header**: Logo, name, address, phone (from settings)
- **Actions**: Save draft, Finalize, Print/Share PDF, Send via WhatsApp, Record payment

### 6.3 Payment Recording
- Select invoice → Record payment
- Amount (default: remaining balance, editable for partial)
- Method: Cash, Card, Bank Transfer, Insurance, Online
- Reference number (optional)
- Date (default: today)
- Auto-update invoice status (partial/paid)

### 6.4 Expense Tracking (P1)
- Add expense: Amount, category, description, date, receipt photo
- Categories: Materials, Rent, Utilities, Salary, Equipment, Maintenance, Marketing, Other
- Monthly expense summary with category breakdown
- Receipt image attachment

### 6.5 Financial Reports (P1)
- Revenue: Daily, weekly, monthly, yearly (by branch, by doctor)
- Expenses: By category, by month
- Profit: Revenue − Expenses
- Outstanding: Unpaid invoice aging report
- Insurance: Claims submitted, approved, pending

---

## 7. Dashboard Module

### 7.1 Dashboard Screen
- **Today's appointments**: Count + list (next 5), tap to expand
- **Revenue today/this week/this month**: Card with amount + trend arrow
- **Patients**: New patients this month, total active
- **Upcoming**: Next 3 appointments with patient name, time, doctor
- **Quick actions**: + New Patient, + New Appointment, + New Invoice
- **Low stock alerts**: Items below reorder level (inventory)
- **Pending tasks**: Unfinished treatments, unsent invoices, pending lab orders

### 7.2 Analytics Screen (P1)
- **Charts**: Revenue trend (line chart), Procedure distribution (pie chart), Appointment volume (bar chart)
- **Date range picker**: Custom, This week, This month, This quarter, This year
- **Filters**: By branch, by doctor
- **Export**: PDF report, CSV data

---

## 8. Staff Management Module (P1)

### 8.1 Staff List
- All staff with role badges, branch, active/inactive status
- Search by name
- Tap → Staff detail

### 8.2 Staff Detail / Edit
- Profile: Name, role, specialization, license number, phone, branch, hire date
- Attendance: Monthly calendar view with status dots
- Performance (P2): Patients treated, revenue generated, appointments completed

### 8.3 Attendance
- Daily check-in/check-out (manual entry by admin)
- Monthly attendance summary: Present, Absent, Late, Leave counts
- Simple — not a full HR system

---

## 9. Inventory Module (P1)

### 9.1 Inventory List
- Group by category or flat list
- Search by name
- Color indicators: Green (OK), Yellow (low), Red (critical)
- Filter: Category, stock level

### 9.2 Item Detail
- Current stock, reorder level, unit cost
- Usage history (linked to procedures)
- Stock log (restocks, adjustments)
- Supplier info

### 9.3 Actions
- **Restock**: Add quantity, record date/supplier/cost
- **Adjust**: Manual correction with reason
- **Alert**: Push notification when stock ≤ reorder level

---

## 10. Insurance Module (P1)

### 10.1 Insurance Companies
- CRUD for insurance companies (name, contact, details)
- Per-company: list of patients covered

### 10.2 Patient Insurance
- Assign insurance policy to patient
- Policy details: Company, policy number, coverage %, annual cap, dates
- Multiple policies per patient supported

### 10.3 Claims
- Create claim from invoice
- Track status: Pending → Submitted → Approved/Rejected → Paid
- Link approved amount back to invoice as insurance payment

---

## 11. Imaging Module (P1)

### 11.1 Capture
- Camera integration: Take intraoral/extraoral photo
- Gallery picker: Select existing images
- Tag: Patient, tooth, appointment, image type

### 11.2 Viewer
- Pinch-to-zoom, rotate
- Side-by-side comparison (before/after)
- Filter by type (X-ray, photo, CBCT)

### 11.3 Storage
- Compressed before upload (quality 80%, max 2048px)
- Thumbnails generated for list views
- Stored in Supabase Storage bucket per clinic

---

## 12. Lab Integration (P1)

### 12.1 Lab Management
- CRUD for labs (name, phone, email, address)

### 12.2 Lab Orders
- Create order from procedure (auto or manual)
- Include: Patient, tooth, shade, material, description
- Track status: Pending → Sent → In Progress → Ready → Delivered
- Expected delivery date, actual delivery date
- Cost tracking (lab fees)

---

## 13. Settings Module

### 13.1 Clinic Settings (Admin only)
- Clinic name, logo, address, phone, email
- Tax ID, currency, default language
- Tooth notation preference (FDI/Universal)
- Working hours template
- Invoice template customization (header/footer text)

### 13.2 Branch Management (Admin only)
- Add/edit/deactivate branches
- Per-branch: Name, address, phone, working hours, chairs

### 13.3 User Profile (All roles)
- Edit own name, phone, photo
- Change password
- Notification preferences
- Language preference

### 13.4 Procedure Catalog Management (Admin/Doctor)
- Add/edit/disable procedures
- Set default prices
- Categorize procedures

---

## 14. Automation Module (P2)

### 14.1 Appointment Reminders
- Configurable timing: 48h, 24h, 2h before
- Channel: SMS (default), WhatsApp (P2)
- Template with variables: `{patient_name}`, `{date}`, `{time}`, `{doctor_name}`, `{clinic_name}`
- Enable/disable globally or per appointment

### 14.2 Birthday Greetings
- Auto-send on patient's birthday
- Channel: SMS or WhatsApp
- Custom message template

### 14.3 Lab Auto-Send
- When procedure status changes to "in_progress" → auto-create lab order
- Configurable per procedure type (not all procedures need lab work)

---

## 15. QR Medical History (P1)

### 15.1 Flow
1. Receptionist taps "Generate QR" on patient detail
2. QR code displayed (encodes a URL with patient token)
3. Patient scans QR → opens mobile web form (hosted as Supabase Edge Function + static HTML)
4. Patient fills medical history form
5. On submit → data saved to Supabase
6. App receives realtime update → medical history refreshed

### 15.2 Web Form
- Mobile-responsive HTML form
- Arabic + English toggle
- Same fields as in-app medical history
- Token-based access (expires after 24h, single-use)
- No login required for patient
