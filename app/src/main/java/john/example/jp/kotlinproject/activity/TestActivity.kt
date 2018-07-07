package john.example.jp.kotlinproject.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import john.example.jp.kotlinproject.R

class TestActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        // Example of a call to a native method
//        sample_text.text = stringFromJNI()
    }
}