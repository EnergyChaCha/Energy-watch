package chacha.enerygy.ganghannal.data.message.dto

class ReportDto {

    var bpm : Float = 0F
    var longitude : Double = 0.0
    var latitude : Double = 0.0

    constructor(bpm: Float, longitude: Double, latitude: Double) {
        this.bpm = bpm
        this.longitude = longitude
        this.latitude = latitude
    }

    override fun toString(): String {
        return "ReportDto(bpm=$bpm, longitude=$longitude, latitude=$latitude)"
    }


}