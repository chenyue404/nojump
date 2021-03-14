package com.chenyue404.nojump.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import android.widget.ToggleButton
import androidx.core.content.edit
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.chenyue404.nojump.MyPreferenceProvider
import com.chenyue404.nojump.R
import com.chenyue404.nojump.dp2Px
import com.chenyue404.nojump.entity.RuleEntity
import com.chenyue404.nojump.fromJson
import com.google.gson.Gson

class RuleFragment : Fragment() {
    private val TAG = "nojump-hook-"

    private lateinit var rvList: RecyclerView
    private lateinit var btSave: ImageButton
    private lateinit var btAdd: ImageButton

    private val dataList = arrayListOf<RuleEntity>()
    private lateinit var listAdapter: RuleListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.fragment_rule, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            rvList = findViewById(R.id.rvList)
            btSave = findViewById(R.id.btSave)
            btAdd = findViewById(R.id.btAdd)
        }
        listAdapter = RuleListAdapter(dataList) {
            dataList.removeAt(it)
            listAdapter.notifyItemRemoved(it)
        }
        rvList.apply {
            addItemDecoration(
                SpaceItemDecoration(
                    10.dp2Px(
                        requireContext()
                    )
                )
            )
            adapter = listAdapter
        }

        btSave.setOnClickListener {
            val haveEmptyEntity =
                dataList.any { it.callPackage.isEmpty() || it.dataString.isEmpty() }
            if (haveEmptyEntity) {
                Toast.makeText(requireContext(), getString(R.string.noEmpty), Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (dataList.isNullOrEmpty()) {
                getSP().edit(true) {
                    putString(MyPreferenceProvider.KEY_NAME, MyPreferenceProvider.EMPTY_STR)
                }
            } else {
                val str = Gson().toJson(dataList)
                getSP().edit(true) {
                    putString(MyPreferenceProvider.KEY_NAME, str)
                }
            }
            startActivity(Intent(requireContext(), EmptyActivity::class.java))
            Toast.makeText(requireContext(), getString(R.string.saved), Toast.LENGTH_SHORT)
                .show()
        }
        btAdd.setOnClickListener {
            dataList.add(RuleEntity())
            listAdapter.notifyItemChanged(dataList.size - 1)
            rvList.scrollToPosition(dataList.size - 1)
        }
        readPerf()
        writeEmptyStr()
    }

    private fun readPerf() {
        val str = getSP().getString(MyPreferenceProvider.KEY_NAME, MyPreferenceProvider.EMPTY_STR)
            ?: return
        if (str == MyPreferenceProvider.EMPTY_STR) return

        val list = fromJson<ArrayList<RuleEntity>>(str)

        dataList.apply {
            clear()
            if (!list.isNullOrEmpty()) {
                addAll(list)
            }
        }
        listAdapter.notifyDataSetChanged()
    }

    private class RuleListAdapter(
        val dataList: ArrayList<RuleEntity>,
        val deleteFun: (Int) -> Unit
    ) :
        RecyclerView.Adapter<RuleListAdapter.ViewHolder>() {

        private class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val etDataString: EditText = itemView.findViewById(R.id.etDataString)
            val etPackage: EditText = itemView.findViewById(R.id.etPackage)
            val tb: ToggleButton = itemView.findViewById(R.id.tb)
            val ibDelete: ImageButton = itemView.findViewById(R.id.ibDelete)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_rule, parent, false)
        )

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val ruleEntity = dataList[position]
            holder.apply {
                etDataString.setText(ruleEntity.dataString)
                etPackage.setText(ruleEntity.callPackage)
                tb.isChecked = ruleEntity.isBlock
                ibDelete.setOnClickListener {
                    deleteFun(position)
                }

                etDataString.doAfterTextChanged {
                    ruleEntity.dataString = it.toString()
                }
                etPackage.doAfterTextChanged {
                    ruleEntity.callPackage = it.toString()
                }
                tb.setOnCheckedChangeListener { buttonView, isChecked ->
                    ruleEntity.isBlock = isChecked
                }
            }
        }

        override fun getItemCount() = dataList.size
    }

    private fun getSP() = requireContext().getSharedPreferences(
        MyPreferenceProvider.PREF_NAME,
        Context.MODE_PRIVATE
    )

    private fun writeEmptyStr() {
        with(getSP()) {
            if (getString(MyPreferenceProvider.KEY_NAME, "").toString().isEmpty()) {
                edit(true) {
                    putString(MyPreferenceProvider.KEY_NAME, MyPreferenceProvider.EMPTY_STR)
                }
            }
        }
    }
}