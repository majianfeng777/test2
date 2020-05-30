package fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.test1.R;

public class controlFragment extends Fragment implements View.OnClickListener {
    private View view;
    private ImageButton btn_up, btn_right, btn_down, btn_left;
    private ImageButton btn_Pause;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.control_fragment,container,false);
        btn_up = (ImageButton)view.findViewById(R.id.btnUp);
        btn_down = (ImageButton)view.findViewById(R.id.btnDown);
        btn_left = (ImageButton)view.findViewById(R.id.btnLeft);
        btn_right = (ImageButton)view.findViewById(R.id.btnRight);
        btn_Pause=(ImageButton)view.findViewById(R.id.btnPause);
        btn_Pause.setOnClickListener(this);
        btn_up.setOnClickListener(this);
        btn_down.setOnClickListener(this);
        btn_left.setOnClickListener(this);
        btn_right.setOnClickListener(this);
        return view;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnUp:
                //开启直播
                //          player.startRealPlay();
                break;
            case R.id.btnDown:

                break;
            case R.id.btnLeft:

                break;
            case R.id.btnRight:

                break;
            case R.id.btnPause:

                break;
        }
    }
}
