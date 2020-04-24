package com.example.dndascension.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.dndascension.R
import com.example.dndascension.models.Campaign
import kotlinx.android.synthetic.main.item_camp.view.*

class CampaignsAdapter (private val context: Context,
                        private val camps: List<Campaign>) : BaseAdapter() {
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return camps.size
    }
    override fun getItem(position: Int): Any {
        return camps[position]
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val c = camps[position]
        val rowView = inflater.inflate(R.layout.item_camp, parent, false)
        rowView.item_camp_name.text = c.camp_name
        rowView.item_camp_tag.text = "${c.created_by}'s Campaign"
        return rowView
    }
}