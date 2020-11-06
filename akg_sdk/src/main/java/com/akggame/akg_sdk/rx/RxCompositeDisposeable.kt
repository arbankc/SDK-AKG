package com.akggame.akg_sdk.rx

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Callable


class RxCompositeDisposableManager() {


    internal interface IRxCallback {
        fun onNext()

        fun onError()

        fun onComplete()
    }

    class RxCallback : IRxCallback {

        override fun onNext() {

        }

        override fun onError() {

        }

        override fun onComplete() {}
    }

    class OnProcess {
        fun onCall() {}
    }

    companion object {
        private var compositeDisposable: CompositeDisposable? = null

        fun add(disposable: Disposable) {
            getCompositeDisposable().add(disposable)
        }

        fun dispose() {
            getCompositeDisposable().dispose()
        }

        fun getCompositeDisposable(): CompositeDisposable {
            if (compositeDisposable == null || compositeDisposable!!.isDisposed) {
                compositeDisposable = CompositeDisposable()
            }
            return compositeDisposable as CompositeDisposable
        }


        //     ------
        fun doAction(onProcess: OnProcess, rxCallback: RxCallback) {
            getCompositeDisposable().add(
                doObservable(onProcess)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(object : DisposableObserver<String>() {


                        override fun onNext(s: String) {
                            rxCallback.onNext()
                        }

                        override fun onError(e: Throwable) {
                            rxCallback.onError()
                        }

                        override fun onComplete() {
                            rxCallback.onComplete()
                        }
                    })
            )

        }

        internal fun doObservable(onProcess: OnProcess): Observable<String> {
            return Observable.defer {
                onProcess.onCall()
                Observable.just("one", "two", "three", "four", "five", "")
            }
        }
    }
}