package fi.climbstationsolutions.climbstation.ui.create.customStep

interface CustomStepFocusListener {
    fun onCustomStepDistanceListener(value: Int, id: Long)
    fun onCustomStepAngleListener(value: Int, id: Long)
}