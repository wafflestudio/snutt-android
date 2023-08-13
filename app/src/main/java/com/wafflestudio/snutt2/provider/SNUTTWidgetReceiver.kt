package com.wafflestudio.snutt2.provider

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.wafflestudio.snutt2.widget.SNUTTWidget

class SNUTTWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = SNUTTWidget()
}
