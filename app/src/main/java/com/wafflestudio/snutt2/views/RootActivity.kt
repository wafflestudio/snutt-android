package com.wafflestudio.snutt2.views

import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.lib.base.BaseActivity
import com.wafflestudio.snutt2.provider.TimetableWidgetProvider
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RootActivity : BaseActivity() {

    @Inject
    lateinit var snuttStorage: SNUTTStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)

        val navController =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
        val navGraph = navController.navInflater.inflate(R.navigation.root_graph)

        navGraph.setStartDestination(
            if (snuttStorage.accessToken.get().isEmpty()) R.id.tutorialFragment
            else R.id.homeFragment
        )

        navController.graph = navGraph

        snuttStorage.accessToken.asObservable()
            .distinctUntilChanged()
            .filter { it.isEmpty() }
            .bindUi(this) {
                TimetableWidgetProvider.refreshWidget(this)
                navController.navigate(R.id.startTutorial)
            }
    }
}
