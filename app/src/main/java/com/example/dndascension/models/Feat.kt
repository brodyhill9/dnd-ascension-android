package com.example.dndascension.models

import com.example.dndascension.interfaces.SetValue

data class Feat (
    override var value_id: Int? = null,
    override var set_value: String = "",
    override var value_desc: String = "",
    override var set_name: String = "Feats"
) : SetValue