package info.dennis_weber.unfima.api.helpers

trait MappableTrait {

  // See: https://codereview.stackexchange.com/a/139436/110626
  Map toMap() {
    this.metaClass.properties.findAll { 'class' != it.name }.collectEntries {
      if (MappableTrait.isAssignableFrom(it.type)) {
        [(it.name): this."$it.name"?.toMap()]
      } else {
        [(it.name): this."$it.name"]
      }
    }
  }

  Map toMapWithoutNull() {
    Map result = [:]
    this.toMap().each {
      if (it.value != null) {
        result.put(it.key, it.value)
      }
    }

    return result
  }
}
