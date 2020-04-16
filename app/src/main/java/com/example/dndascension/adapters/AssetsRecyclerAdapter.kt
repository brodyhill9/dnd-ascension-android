
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.dndascension.R
import com.example.dndascension.fragments.AssetDialogFragment
import com.example.dndascension.interfaces.Asset
import com.example.dndascension.utils.AssetDialogFragmentType
import kotlinx.android.synthetic.main.item_asset.view.*

class AssetsRecyclerAdapter(private val context: Context, private val assets: List<Asset>) :
    RecyclerView.Adapter<AssetsRecyclerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_asset,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return assets.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val asset = assets[position]
        holder.name?.text = asset.name()
        holder.tag?.text = asset.tag()
        holder.desc?.text = asset.desc()
        holder.itemView.setOnClickListener {
            val fragment = AssetDialogFragment(asset, AssetDialogFragmentType.RemoveAsset)
            fragment.show((context as AppCompatActivity).supportFragmentManager, fragment.tag)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name = view.item_asset_name
        val tag = view.item_asset_tag
        val desc = view.item_asset_desc
    }
}