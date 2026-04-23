package com.alaaeltaweel.thikrallah.quran.labs.androidquran.ui.translation;

import com.alaaeltaweel.thikrallah.quran.labs.androidquran.common.LocalTranslation;
import com.alaaeltaweel.thikrallah.quran.labs.androidquran.common.QuranAyahInfo;

public interface OnTranslationActionListener {
  void onTranslationAction(QuranAyahInfo ayah, LocalTranslation[] translations, int actionId);
}
