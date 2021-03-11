package com.chenyue404.nojump.ui

import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.chenyue404.nojump.LogReceiver
import com.chenyue404.nojump.R
import com.chenyue404.nojump.dp2Px
import com.chenyue404.nojump.entity.LogEntity
import com.chenyue404.nojump.timeToStr

class LogFragment : Fragment() {
    private val TAG = "nojump-hook-"

    private lateinit var rvList: RecyclerView
    private lateinit var btClear: ImageButton

    private lateinit var logReceiver: LogReceiver
    private val dataList = arrayListOf<LogEntity>()
    private val listAdapter = LogListAdapter(dataList)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.fragment_log, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            rvList = findViewById(R.id.rvList)
            btClear = findViewById(R.id.btClear)
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

        btClear.setOnClickListener {
            dataList.clear()
            listAdapter.notifyDataSetChanged()
        }

        logReceiver = LogReceiver {
            dataList.add(it)
            listAdapter.notifyItemChanged(dataList.size - 1)
            rvList.scrollToPosition(dataList.size - 1)
        }
        requireActivity().registerReceiver(logReceiver, IntentFilter().apply {
            addAction(LogReceiver.ACTION)
        })
    }

    override fun onDestroy() {
        requireActivity().unregisterReceiver(logReceiver)
        super.onDestroy()
    }

    class LogListAdapter(val dataList: ArrayList<LogEntity>) :
        RecyclerView.Adapter<LogListAdapter.ViewHolder>() {
        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvTime = itemView.findViewById<TextView>(R.id.tvTime)
            val tvCallingPackage = itemView.findViewById<TextView>(R.id.tvCallingPackage)
            val tvDataString = itemView.findViewById<TextView>(R.id.tvDataString)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_log, parent, false)
        )

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val logEntity = dataList[position]
            holder.apply {
                tvTime.text = logEntity.time.timeToStr()
                tvCallingPackage.text = logEntity.callPackage
                tvDataString.text = logEntity.dataString
                itemView.setBackgroundColor(if (logEntity.blocked) Color.RED else Color.LTGRAY)
            }
        }

        override fun getItemCount() = dataList.size
    }
}