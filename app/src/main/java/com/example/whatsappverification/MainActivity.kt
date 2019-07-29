package com.example.whatsappverification

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.facebook.accountkit.*
import com.facebook.accountkit.ui.AccountKitActivity
import com.facebook.accountkit.ui.AccountKitConfiguration
import com.facebook.accountkit.ui.LoginType


//2jmj7l5rSw0yVb/vlWAYkK/YBwk=
class MainActivity : AppCompatActivity() {

    companion object {
        val APP_REQUEST_CODE = 99
    }

    private var accessToken: AccessToken? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }
    fun onLoginPhone(view: View) {
        accessToken = AccountKit.getCurrentAccessToken()
        if (accessToken != null) {
            //Handle Returning User
        } else {
            phoneLogin()
            //Handle new or logged out user
        }
    }
    private fun phoneLogin() {
        val intent = Intent(this, AccountKitActivity::class.java)
        val configurationBuilder = AccountKitConfiguration.AccountKitConfigurationBuilder(
            LoginType.PHONE,
            AccountKitActivity.ResponseType.TOKEN
        )
        configurationBuilder.setSMSWhitelist(arrayOf("IN", "US"))
        configurationBuilder.setDefaultCountryCode("IN")
        val configuration = configurationBuilder.build()

        intent.putExtra(
            AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
            configuration
        )
        startActivityForResult(intent, APP_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == APP_REQUEST_CODE) {
            if (data != null) {
                val loginResult: AccountKitLoginResult? = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY)
                var toastMessage: String = ""
                loginResult?.let {
                    if (it.wasCancelled()) {
                        toastMessage = "Login Cancelled"
                    } else if (it.error != null) {
                        val intent = Intent(this, ErrorActivity::class.java)
                        intent.putExtra(ErrorActivity.HELLO_TOKEN_ACTIVITY_ERROR_EXTRA, loginResult.error)
                        startActivity(intent)

                    } else {
                        val accessToken = it.accessToken
                        val tokenRefresh = it.tokenRefreshIntervalInSeconds
                        if (accessToken != null) {
                            toastMessage = ("Success:" + accessToken.accountId
                                    + tokenRefresh)
                            startActivity(Intent(this, TokenActivity::class.java))
                        } else {
                            toastMessage = "Unknown response type"
                            getInfo()
                        }
                    }


                }
                Toast.makeText(
                    this,
                    toastMessage,
                    Toast.LENGTH_LONG
                ).show()
            }


        }
    }

    private fun getInfo() {

        AccountKit.getCurrentAccount(object : AccountKitCallback<Account> {
            override fun onSuccess(account: Account) {
                // Get Account Kit ID
                val accountKitId = account.id

                // Get phone number
                val phoneNumber = account.phoneNumber
                if (phoneNumber != null) {
                    val phoneNumberString = phoneNumber.toString()
                }

                // Get email
                val email = account.email
                Toast.makeText(this@MainActivity, "acc id$accountKitId $phoneNumber ", Toast.LENGTH_LONG).show()
            }

            override fun onError(error: AccountKitError) {
                Toast.makeText(this@MainActivity, "error", Toast.LENGTH_LONG).show()

                // Handle Error
            }
        })
    }
}
