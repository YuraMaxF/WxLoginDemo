package com.yiqi.muying.wxapi

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.yiqi.muying.Configs
import com.yiqi.muying.putStringSp
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.lang.StringBuilder

class WXEntryActivity : AppCompatActivity(),
    IWXAPIEventHandler {

    private lateinit var iwxapi: IWXAPI
    private val mProgressDialog by lazy {
        ProgressDialog(this).run {
            setProgressStyle(ProgressDialog.STYLE_SPINNER) //转盘
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            setTitle("提示")
            setMessage("登录中，请稍后")
            this
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("","进入 WXEntryActivity")
        supportActionBar?.hide()
        window?.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        //接收到分享以及登录的intent传递handleIntent方法，处理结果
        iwxapi = WXAPIFactory.createWXAPI(this, Configs.APP_ID, true).run {
            handleIntent(intent!!, this@WXEntryActivity)
            this
        }
    }

    override fun onResp(p0: BaseResp?) {
        when (p0!!.errCode) {
            BaseResp.ErrCode.ERR_OK -> { //获取 access_token
                getAccessToken((p0 as SendAuth.Resp).code)
            }
            BaseResp.ErrCode.ERR_AUTH_DENIED -> finish() //用户拒绝授权
            BaseResp.ErrCode.ERR_USER_CANCEL -> finish() //用户取消
        }
    }

    override fun onReq(p0: BaseReq?) {

    }

    private fun getAccessToken(code: String) {
        mProgressDialog.show()
        //获取授权
        val loginUrl = StringBuilder().run {
            append("https://api.weixin.qq.com/sns/oauth2/access_token")
            append("?appid=")
            append(Configs.APP_ID)
            append("&secret=")
            append(Configs.APP_SERECET)
            append("&code=")
            append(code)
            append("&grant_type=authorization_code")
        }
        Log.e("loginUrl", loginUrl.toString())

        OkHttpClient().newCall(
            Request.Builder()
                .url(loginUrl.toString())
                .get() //默认就是GET请求，可以不写
                .build()
        ).apply {
            enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    response.body?.string()?.let {
                        Log.e("onResponse", "onResponse: $it")
                        var access = ""
                        var openId = ""
                        try {
                            val jsonObject = JSONObject(it).apply {
                                access = getString("access_token")
                                openId = getString("openid")
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                        getUserInfo(access, openId)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    mProgressDialog.dismiss()
                }

            })
        }
    }

    private fun getUserInfo(access: String, openid: String) {
        val getUserInfoUrl =
            "https://api.weixin.qq.com/sns/userinfo?access_token=$access&openid=$openid"
        OkHttpClient().newCall(
            Request.Builder()
                .url(getUserInfoUrl)
                .get()//默认就是GET请求，可以不写
                .build()
        ).apply {
            enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    mProgressDialog.dismiss()
                }

                override fun onResponse(call: Call, response: Response) {
                    response.body?.string()?.let {
                        Log.e("onResponse", "onResponse: $it")
                        putStringSp("responseInfo", it)
                    }
                    finish()
                    mProgressDialog.dismiss()
                }
            })
        }
    }
}