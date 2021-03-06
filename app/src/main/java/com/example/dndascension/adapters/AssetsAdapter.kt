package com.example.dndascension.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.dndascension.R
import com.example.dndascension.interfaces.Asset
import kotlinx.android.synthetic.main.item_asset.view.*

class AssetsAdapter (private val context: Context,
                      private val assets: List<Asset>) : BaseAdapter() {
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return assets.size
    }
    override fun getItem(position: Int): Any {
        return assets[position]
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val asset = assets.get(position)
        val rowView = inflater.inflate(R.layout.item_asset, parent, false)

        rowView.item_asset_name.text = asset.name()
        rowView.item_asset_tag.text = asset.tag()
        rowView.item_asset_desc.text = asset.desc()

        return rowView
    }
}