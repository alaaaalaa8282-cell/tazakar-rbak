package com.alaaeltaweel.thikrallah.quran.common.search.arabic

object ArabicCharacterHelper {
  private val lookupTable = hashMapOf(
      // given: ا
      // match: آأإاﻯ
      "\u0627" to "\u0622\u0623\u0625\u0627\u0649",

      // given: ﺃ
      // match: ﺃﺀﺆﺋ
      "\u0623" to "\u0621\u0623\u0624\u0626",

      // given: ﺀ
      // match: ﺀﺃﺆ
      "\u0621" to "\u0621\u0623\u0624\u0626",

      // given: ﺕ
      // match: ﺕﺓ
      "\u062a" to "\u062a\u0629",

      // given: ﺓ
      // match: ﺓتﻫ
      "\u0629" to "\u0629\u062a\u0647",

      // given: ه
      // match: ةه
      "\u0647" to "\u0647\u0629",

      // given: ﻯ
      // match: ﻯي
      "\u0649" to "\u0649\u064a"
  )

  fun generateRegex(query: String): String {
    val characters = query.toCharArray()
    val regexBuilder = StringBuilder()
    characters.forEach {
      if (lookupTable.containsKey(it.toString())) {
        regexBuilder.append("[")
        regexBuilder.append(lookupTable[it.toString()])
        regexBuilder.append("]")
      } else {
        regexBuilder.append(it)
      }
    }
    return regexBuilder.toString()
  }
}
