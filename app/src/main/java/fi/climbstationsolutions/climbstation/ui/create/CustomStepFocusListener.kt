package fi.climbstationsolutions.climbstation.ui.create

interface CustomStepFocusListener {
    fun onCustomStepDistanceListener(value: String, id: Long)
    fun onCustomStepAngleListener(value: String, id: Long)
}