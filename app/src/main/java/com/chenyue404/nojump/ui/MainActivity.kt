package com.chenyue404.nojump.ui

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.chenyue404.nojump.R
import com.shizhefei.view.indicator.FixedIndicatorView
import com.shizhefei.view.indicator.IndicatorViewPager
import com.shizhefei.view.indicator.transition.OnTransitionTextListener

class MainActivity : AppCompatActivity() {
    private lateinit var fivTop: FixedIndicatorView
    private lateinit var vpContent: ViewPager

    private val logFragment by lazy { LogFragment() }
    private val ruleFragment by lazy { RuleFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fivTop = findViewById(R.id.fivTop)
        vpContent = findViewById(R.id.vpContent)

        fivTop.onTransitionListener = OnTransitionTextListener()
            .setColor(Color.BLACK, Color.GRAY)
            .setSize(20f, 18f)
        IndicatorViewPager(fivTop, vpContent)
            .adapter =
            object : IndicatorViewPager.IndicatorFragmentPagerAdapter(supportFragmentManager) {
                override fun getCount() = 2

                override fun getViewForTab(
                    position: Int,
                    convertView: View?,
                    container: ViewGroup?
                ): View {
                    val textView =
                        convertView?.let { it as TextView }
                            ?: TextView(this@MainActivity)

                    textView.apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        gravity = Gravity.CENTER
                        setText(if (position == 0) R.string.log else R.string.rule)
                    }

                    return textView
                }

                override fun getFragmentForPage(position: Int) =
                    if (position == 0) logFragment else ruleFragment
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.about -> {
                val address = getString(R.string.githubAddress)
                AlertDialog.Builder(this)
                    .setTitle(R.string.about)
                    .setMessage(address)
                    .setPositiveButton(
                        R.string.view
                    ) { dialog, which ->
                        startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(address)
                            )
                        )
                    }
                    .create()
                    .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}