package com.wafflestudio.snutt2.lib.rx

import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.jakewharton.rxbinding4.internal.checkMainThread
import io.reactivex.rxjava3.android.MainThreadDisposable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer

fun BottomNavigationView.itemSelected(): Observable<MenuItem> {
    return ItemSelectedObservable(this)
}

private class ItemSelectedObservable(
    private val view: NavigationBarView
) : Observable<MenuItem>() {

    override fun subscribeActual(observer: Observer<in MenuItem>) {
        if (!checkMainThread(observer)) {
            return
        }
        val listener = Listener(view, observer)
        observer.onSubscribe(listener)
        view.setOnItemSelectedListener(listener)
    }

    private class Listener(
        private val view: NavigationBarView,
        private val observer: Observer<in MenuItem>
    ) : MainThreadDisposable(), NavigationBarView.OnItemSelectedListener {

        override fun onDispose() {
            view.setOnItemSelectedListener(null)
        }

        override fun onNavigationItemSelected(item: MenuItem): Boolean {
            observer.onNext(item)
            return true
        }
    }
}
