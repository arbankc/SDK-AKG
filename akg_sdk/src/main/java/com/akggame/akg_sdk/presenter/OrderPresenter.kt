package com.akggame.akg_sdk.presenter
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.akggame.akg_sdk.dao.MainDao
import com.akggame.akg_sdk.dao.api.model.response.BaseResponse
import com.akggame.akg_sdk.dao.api.model.response.DepositResponse
import com.akggame.akg_sdk.rx.IView
import com.akggame.akg_sdk.rx.RxObserver
import com.akggame.akg_sdk.ui.activity.OttopayIView
import com.akggame.akg_sdk.ui.dialog.register.OTPIView
import com.clappingape.dplkagent.model.api.request.DepositRequest
import io.reactivex.disposables.Disposable

class OrderPresenter(val mIView : IView) {

    fun onCreateDeposit(body:DepositRequest,context: Context){
        MainDao().onCreateDeposit(context,body).subscribe(object: RxObserver<BaseResponse>(mIView,""){
            override fun onSubscribe(d: Disposable) {
                super.onSubscribe(d)
                Log.d("TESTING API", "onSubscribe")
            }

            override fun onComplete() {
                super.onComplete()
                Log.d("TESTING API", "onComplete")
            }

            override fun onNext(t: BaseResponse) {
                super.onNext(t)
                Log.d("TESTING API", "onNext")
                t as DepositResponse
                if (t.meta.status) {
                    (mIView as OttopayIView).doOnSuccessCreateDeposit(t as DepositResponse)
                } else {
                    Toast.makeText(context, t.BaseDataResponse?.message, Toast.LENGTH_LONG).show()
                }
            }

            override fun onError(e: Throwable) {
                super.onError(e)
                Log.d("TESTING API", "onError: "+ e.toString())
            }
        })
    }
}