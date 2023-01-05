package com.shaohuasun.branchtest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.shaohuasun.branchtest.databinding.ActivityMainBinding
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.Branch
import io.branch.referral.BranchError
import io.branch.referral.SharingHelper
import io.branch.referral.util.ContentMetadata
import io.branch.referral.util.LinkProperties
import io.branch.referral.util.ShareSheetStyle

class MainActivity : AppCompatActivity() {
    companion object {
        val KEY_DEEP_LINK_PARAM = "deep_link_test"
        val TAG = "Branch_SDK"
    }

    private lateinit var binding: ActivityMainBinding

    private val branchListener = Branch.BranchReferralInitListener { referringParams, error ->
        if (error == null) {
            val rawString = referringParams.toString()
            Log.i(TAG, rawString)
            val key = referringParams?.optString(KEY_DEEP_LINK_PARAM) ?: ""

            if (key == OtherActivity.KEY_DIRECT) {
                startOther(rawString)
            }
        } else {
            Log.e(TAG, error.message)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.share.setOnClickListener {
            share()
        }
    }

    override fun onStart() {
        super.onStart()
        Branch.sessionBuilder(this).withCallback(branchListener).withData(this.intent?.data).init()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        this.intent = intent
        // Branch reinit (in case Activity is already in foreground when Branch link is clicked)
        Branch.sessionBuilder(this).withCallback(branchListener).reInit()
    }

    private fun share() {
        val buo = BranchUniversalObject()
            .setCanonicalIdentifier("shaohuasun_cat")
            .setTitle("Check out this cute cat!")
            .setContentDescription("What a cute cat this is")
            .setContentImageUrl("https://img2.doubanio.com/icon/ul6609719-11.jpg")
            .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
            .setLocalIndexMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
            .setContentMetadata(ContentMetadata().addCustomMetadata("harp", "seal"))

        val lp = LinkProperties()
            .setChannel("facebook")
            .setFeature("sharing")
            .setCampaign("content 123 launch")
            .setStage("new user")
            .addControlParameter("\$desktop_url", "http://example.com/home")
            .addControlParameter(KEY_DEEP_LINK_PARAM, OtherActivity.KEY_DIRECT)

        val ss = ShareSheetStyle(
            this@MainActivity,
            "Check this cute thing!",
            "This stuff is interesting: "
        )
            .setCopyUrlStyle(
                resources.getDrawable(android.R.drawable.ic_menu_send),
                "Copy",
                "Added to clipboard"
            )
            .setMoreOptionStyle(
                resources.getDrawable(android.R.drawable.ic_menu_search),
                "Show more"
            )
            .addPreferredSharingOption(SharingHelper.SHARE_WITH.FACEBOOK)
            .addPreferredSharingOption(SharingHelper.SHARE_WITH.EMAIL)
            .addPreferredSharingOption(SharingHelper.SHARE_WITH.MESSAGE)
            .addPreferredSharingOption(SharingHelper.SHARE_WITH.HANGOUT)
            .setAsFullWidthStyle(true)
            .setSharingTitle("Share With")

        buo.showShareSheet(this, lp, ss, object : Branch.BranchLinkShareListener {
            override fun onShareLinkDialogLaunched() {}
            override fun onShareLinkDialogDismissed() {}
            override fun onLinkShareResponse(
                sharedLink: String?,
                sharedChannel: String?,
                error: BranchError?
            ) {
                Log.e(TAG, error?.message ?: "")
            }

            override fun onChannelSelected(channelName: String) {}
        })
    }

    private fun startOther(content: String) {
        startActivity(Intent(this, OtherActivity::class.java).apply {
            putExtra(OtherActivity.KEY_CONTENT, content)
        })
    }
}