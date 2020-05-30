package fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.test1.R;
import com.example.test1.item.gridViewStore_item;

import java.util.ArrayList;
import java.util.List;

public class storeFragment extends Fragment {
    private List<gridViewStore_item> list=new ArrayList<>();
    private gridviewStore_adapter adapter;
    private GridView gridView;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.store_fragment,container,false);
        initData();
        adapter=new gridviewStore_adapter();
        adapter.setData(list);
        gridView=(GridView)view.findViewById(R.id.gridview_store);
        gridView.setAdapter(adapter);
        return view;
    }
    private void initData(){
        for (int i=0;i<10;i++){
//            gridViewStore_item a=new gridViewStore_item();
//            list.add(a);
            gridViewStore_item b=new gridViewStore_item("6501",R.drawable.ic_launcher_foreground);
            list.add(b);
//            gridViewStore_item c=new gridViewStore_item("6501",R.drawable.ic_launcher_foreground);
//            list.add(c);
        }

    }
}
