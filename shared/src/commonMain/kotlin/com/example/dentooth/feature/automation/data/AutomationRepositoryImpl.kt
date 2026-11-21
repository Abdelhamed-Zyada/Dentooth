package com.example.dentooth.feature.automation.data

import com.example.dentooth.feature.automation.domain.model.AutomationChannel
import com.example.dentooth.feature.automation.domain.model.AutomationLog
import com.example.dentooth.feature.automation.domain.model.AutomationLogStatus
import com.example.dentooth.feature.automation.domain.model.AutomationRule
import com.example.dentooth.feature.automation.domain.model.AutomationRuleType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AutomationRepositoryImpl : AutomationRepository {

    private val rulesFlow = MutableStateFlow(seedRules())
    private val logsFlow = MutableStateFlow(seedLogs())

    override fun getRules(): Flow<List<AutomationRule>> = rulesFlow.asStateFlow()

    override fun getLogs(): Flow<List<AutomationLog>> = logsFlow.asStateFlow()

    override suspend fun toggleRule(ruleId: String, isEnabled: Boolean): Result<Unit> {
        rulesFlow.update { current ->
            current.map { rule ->
                if (rule.id == ruleId) rule.copy(isEnabled = isEnabled) else rule
            }
        }
        return Result.success(Unit)
    }

    override suspend fun updateRuleTemplates(
        ruleId: String,
        englishTemplate: String,
        arabicTemplate: String
    ): Result<Unit> {
        rulesFlow.update { current ->
            current.map { rule ->
                if (rule.id == ruleId) {
                    rule.copy(
                        templateEnglish = englishTemplate,
                        templateArabic = arabicTemplate
                    )
                } else rule
            }
        }
        return Result.success(Unit)
    }

    override suspend fun triggerManualTest(
        ruleId: String,
        patientPhone: String,
        patientName: String,
        channel: AutomationChannel
    ): Result<AutomationLog> {
        val rule = rulesFlow.value.find { it.id == ruleId }
            ?: return Result.failure(IllegalArgumentException("Rule not found"))

        val content = rule.templateEnglish
            .replace("{patient_name}", patientName)
            .replace("{doctor_name}", "Dr. Ahmed Mostafa")
            .replace("{branch_name}", "New Cairo Main")
            .replace("{time}", "5:30 PM")
            .replace("{amount}", "1,200")
            .replace("{receipt_url}", "https://dentobrain.app/inv/9012")

        val log = AutomationLog(
            id = "log_${kotlin.random.Random.nextInt(10000, 99999)}",
            ruleId = rule.id,
            ruleTitle = rule.title,
            patientId = "test_pt",
            patientName = patientName,
            patientPhone = patientPhone,
            channel = channel,
            messageContent = content,
            status = AutomationLogStatus.DELIVERED,
            timestamp = "Today, 10:15 AM",
            costEstimateEgp = if (channel == AutomationChannel.WHATSAPP) 0.85 else 0.40
        )

        logsFlow.update { listOf(log) + it }
        rulesFlow.update { current ->
            current.map {
                if (it.id == ruleId) it.copy(
                    totalSentCount = it.totalSentCount + 1,
                    lastTriggeredAt = "Just now"
                ) else it
            }
        }

        return Result.success(log)
    }

    override suspend fun clearLogs(): Result<Unit> {
        logsFlow.value = emptyList()
        return Result.success(Unit)
    }

    private fun seedRules(): List<AutomationRule> = listOf(
        AutomationRule(
            id = "rule_24h",
            type = AutomationRuleType.APPOINTMENT_REMINDER_24H,
            title = "24h Pre-Appointment Confirmation",
            description = "Sends interactive WhatsApp/SMS 24 hours prior to appointment time requesting confirmation.",
            channel = AutomationChannel.WHATSAPP,
            isEnabled = true,
            templateEnglish = "Dear {patient_name}, reminder of your dental appointment with {doctor_name} at DentoBrain ({branch_name}) tomorrow at {time}. Reply 1 to confirm or 2 to reschedule.",
            templateArabic = "أهلاً بك {patient_name}، نذكرك بموعدك بعيادة دنتوبرين ({branch_name}) غداً الساعة {time} مع {doctor_name}. لتأكيد الحضور أرسل 1 أو 2 للتأجيل.",
            totalSentCount = 428,
            lastTriggeredAt = "Today, 09:00 AM"
        ),
        AutomationRule(
            id = "rule_2h",
            type = AutomationRuleType.APPOINTMENT_REMINDER_2H,
            title = "2h Clinic Arrival & Location Reminder",
            description = "Dispatches SMS with branch GPS directions 2 hours prior to scheduled chair time.",
            channel = AutomationChannel.SMS,
            isEnabled = true,
            templateEnglish = "Hi {patient_name}, we are preparing your dental suite! Your appointment starts in 2 hours at {branch_name}. Clinic GPS: https://maps.app.goo.gl/dentobrain",
            templateArabic = "مرحباً {patient_name}، نحن بانتظارك! موعدك اليوم بعد ساعتين بفرع {branch_name}. رابط موقع العيادة: https://maps.app.goo.gl/dentobrain",
            totalSentCount = 380,
            lastTriggeredAt = "Today, 01:00 PM"
        ),
        AutomationRule(
            id = "rule_post_op",
            type = AutomationRuleType.POST_OP_CARE_FOLLOWUP,
            title = "Post-Operative Care Follow-up",
            description = "Sends recovery checklist 6 hours post surgical extraction, root canal, or implant placement.",
            channel = AutomationChannel.WHATSAPP,
            isEnabled = true,
            templateEnglish = "Dear {patient_name}, Dr. {doctor_name} hopes you are feeling comfortable after today's procedure. Remember: avoid hot food/drinks and take prescribed medication. Emergency line: 19888.",
            templateArabic = "عزيزي {patient_name}، يتمنى لك د. {doctor_name} الشفاء العاجل بعد إجراء اليوم. برجاء الالتزام بالتعليمات والمسكنات الموصوفة. خط الطوارئ: 19888.",
            totalSentCount = 142,
            lastTriggeredAt = "Yesterday, 07:30 PM"
        ),
        AutomationRule(
            id = "rule_birthday",
            type = AutomationRuleType.BIRTHDAY_GREETING,
            title = "Patient Birthday Celebration & Voucher",
            description = "Dispatches festive WhatsApp greeting on patient birthday with 20% clinic voucher code.",
            channel = AutomationChannel.WHATSAPP,
            isEnabled = true,
            templateEnglish = "Happy Birthday {patient_name}! 🎂 The DentoBrain team wishes you a wonderful year. Enjoy a complimentary 20% celebration voucher on teeth whitening or cleaning valid this month! Code: BDAY20",
            templateArabic = "كل عام وأنت بخير بمناسبة عيد ميلادك {patient_name}! 🎂 أسرة دنتوبرين تتمنى لك عاماً رائعاً، ويسعدنا تقديم قسيمة خصم 20% على تبييض أو تنظيف الأسنان طوال الشهر! كود: BDAY20",
            totalSentCount = 95,
            lastTriggeredAt = "2 days ago"
        ),
        AutomationRule(
            id = "rule_recall",
            type = AutomationRuleType.RECALL_DUE_6M,
            title = "6-Month Hygiene & Scaling Recall",
            description = "Re-engages patients who have not had a checkup in 6 months for preventive care.",
            channel = AutomationChannel.SMS,
            isEnabled = false,
            templateEnglish = "Hi {patient_name}, it has been 6 months since your last dental cleaning! Keep your smile shining bright and book your routine prophylaxis checkup today at DentoBrain.",
            templateArabic = "أهلاً {patient_name}، مر 6 أشهر على آخر جلسة تنظيف أسنان وفحص! احرص على حماية ابتسامتك واحجز فحصك الدوري اليوم في دنتوبرين.",
            totalSentCount = 210,
            lastTriggeredAt = "Nov 12, 2026"
        ),
        AutomationRule(
            id = "rule_lab_ready",
            type = AutomationRuleType.LAB_READY_PATIENT_ALERT,
            title = "Dental Lab Prosthesis Arrival Alert",
            description = "Triggered automatically when a technician marks an order as DELIVERED to schedule seating.",
            channel = AutomationChannel.WHATSAPP,
            isEnabled = true,
            templateEnglish = "Great news {patient_name}! Your custom dental restoration has arrived from the laboratory and passed quality inspection. Call us to schedule your fitting appointment.",
            templateArabic = "خبر رائع {patient_name}! تركيبة أسنانك وصلت من المعمل واجتازت فحص الجودة بنجاح. يرجى التواصل معنا لتحديد موعد التركيب النهائي.",
            totalSentCount = 67,
            lastTriggeredAt = "Today, 11:45 AM"
        ),
        AutomationRule(
            id = "rule_receipt",
            type = AutomationRuleType.PAYMENT_RECEIPT,
            title = "Instant Payment E-Receipt Notification",
            description = "Sends digital receipt link immediately after any billing settlement.",
            channel = AutomationChannel.SMS,
            isEnabled = true,
            templateEnglish = "Thank you {patient_name}. Payment of {amount} EGP received at DentoBrain ({branch_name}). View digital receipt: {receipt_url}",
            templateArabic = "شكراً لك {patient_name}. تم استلام دفعة بقيمة {amount} ج.م في عيادة دنتوبرين ({branch_name}). رابط الإيصال الإلكتروني: {receipt_url}",
            totalSentCount = 512,
            lastTriggeredAt = "Today, 02:20 PM"
        )
    )

    private fun seedLogs(): List<AutomationLog> = listOf(
        AutomationLog(
            id = "log_1",
            ruleId = "rule_24h",
            ruleTitle = "24h Pre-Appointment Confirmation",
            patientId = "pat_1",
            patientName = "Omar Farouk",
            patientPhone = "+201012345678",
            channel = AutomationChannel.WHATSAPP,
            messageContent = "Dear Omar Farouk, reminder of your dental appointment with Dr. Ahmed Mostafa at DentoBrain (New Cairo Main) tomorrow at 4:30 PM. Reply 1 to confirm or 2 to reschedule.",
            status = AutomationLogStatus.READ,
            timestamp = "Today, 09:00 AM",
            costEstimateEgp = 0.85
        ),
        AutomationLog(
            id = "log_2",
            ruleId = "rule_lab_ready",
            ruleTitle = "Dental Lab Prosthesis Arrival Alert",
            patientId = "pat_2",
            patientName = "Nour El-Sherif",
            patientPhone = "+201198765432",
            channel = AutomationChannel.WHATSAPP,
            messageContent = "Great news Nour El-Sherif! Your custom dental restoration has arrived from the laboratory and passed quality inspection. Call us to schedule your fitting appointment.",
            status = AutomationLogStatus.DELIVERED,
            timestamp = "Today, 11:45 AM",
            costEstimateEgp = 0.85
        ),
        AutomationLog(
            id = "log_3",
            ruleId = "rule_2h",
            ruleTitle = "2h Clinic Arrival & Location Reminder",
            patientId = "pat_3",
            patientName = "Hassan Youssef",
            patientPhone = "+201234567890",
            channel = AutomationChannel.SMS,
            messageContent = "Hi Hassan Youssef, we are preparing your dental suite! Your appointment starts in 2 hours at New Cairo Main. Clinic GPS: https://maps.app.goo.gl/dentooth",
            status = AutomationLogStatus.SENT,
            timestamp = "Today, 01:00 PM",
            costEstimateEgp = 0.40
        ),
        AutomationLog(
            id = "log_4",
            ruleId = "rule_post_op",
            ruleTitle = "Post-Operative Care Follow-up",
            patientId = "pat_4",
            patientName = "Layla Mourad",
            patientPhone = "+201055566778",
            channel = AutomationChannel.WHATSAPP,
            messageContent = "Dear Layla Mourad, Dr. Ahmed Mostafa hopes you are feeling comfortable after today's procedure. Remember: avoid hot food/drinks and take prescribed medication. Emergency line: 19888.",
            status = AutomationLogStatus.READ,
            timestamp = "Yesterday, 07:30 PM",
            costEstimateEgp = 0.85
        )
    )
}
