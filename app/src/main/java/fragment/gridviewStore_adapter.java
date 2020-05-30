package fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.test1.R;
import com.example.test1.item.gridViewStore_item;

import java.util.ArrayList;
import java.util.List;

public class gridviewStore_adapter extends BaseAdapter {
    private List<gridViewStore_item> list=new ArrayList<>();
    public void  setData(List<gridViewStore_item> list){
        this.list=list;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.store_fragment_item,null);
        TextView textView=view.findViewById(R.id.text_storefragment_item);
        Button button=view.findViewById(R.id.btn_storefragment_item);

        textView.setText(list.get(position).getText());
        button.setBackgroundResource(list.get(position).getImageId());
        return view;
    }
}
