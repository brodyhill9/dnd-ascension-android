package com.example.dndascension.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.dndascension.R
import com.example.dndascension.models.Character
import kotlinx.android.synthetic.main.item_char.view.*

class CharactersAdapter (private val context: Context,
                         private val chars: List<Character>) : BaseAdapter() {
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return chars.size
    }
    override fun getItem(position: Int): Any {
        return chars[position]
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val c = chars[position]
        val rowView = inflater.inflate(R.layout.item_char, parent, false)
        rowView.item_char_name.text = c.char_name
        rowView.item_char_tag.text = c.raceClassLevel()
        return rowView
    }
}