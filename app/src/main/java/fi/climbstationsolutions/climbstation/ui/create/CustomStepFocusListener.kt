package fi.climbstationsolutions.climbstation.ui.create

interface CustomStepFocusListener {
    fun onCustomStepDistanceListener(value: Int, id: Long)
    fun onCustomStepAngleListener(value: Int, id: Long)
}