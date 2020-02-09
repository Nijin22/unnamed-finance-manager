package info.dennis_weber.unfima.api.services

class TimestampHelper {
  static long getCurrentTimestamp() {
    return (new Date().getTime() / 1000)
  }
}
