package com.vroste.playjsonoptics

import play.api.libs.json.{Format, JsPath, JsValue}

object Helpers {
  implicit class JsPathExtensions(path: JsPath) {
    def moveTo(newPath: JsPath): JsValue => JsValue = {
      val atOldPath = JsLens.optional[JsValue](path)
      val atNewPath = JsLens.optional[JsValue](newPath)

      (json: JsValue) =>
        atOldPath
          .getOption(json)
          .flatten
          .fold(identity[JsValue] _) { value =>
            atNewPath.set(Some(value)) andThen atOldPath.set(None)
          }(json)
    }

    def setDefault[T : Format](defaultValue: T): JsValue => JsValue =
      JsLens.optional[T](path) modify (_ orElse Some(defaultValue))
  }
}
