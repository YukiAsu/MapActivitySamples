package jp.dip.sugarhouse.geoCodingMapActivity;

import java.util.List;
import java.util.Locale;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

/**
 * MapView������T���v���I<br>
 * ���ł��邱�ƁF<br>
 * �E��[�̃e�L�X�g�{�b�N�X�ɓ��͂������e�����A���^�C���i1�b���ƂɁj�W�I�R�[�f�B���O���āA���̒n�_���Z���^�����O<br>
 * �E�Y�[���R���g���[�����E�ɂ�����Ƃ��炷�B
 * @author YukiAsu
 */
public class GeoCodingMapActivity extends MapActivity implements TextWatcher{

	/** 
	 * ��ʏ㕔�̓��͗p�{�b�N�X
	 */
	private EditText edit;
	/**
	 * �n�}
	 */
	private MapView mapView;
	/**
	 * �}�b�v�R���g���[���B�R������Ē��S�_�ړ���Y�[�����s���B
	 */
	private MapController control;

	// ���C�A�E�g�p�̒萔�B�����̂ŁB
    private final int FP = ViewGroup.LayoutParams.FILL_PARENT; 
    private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

    	// �n�����͕���MapView�������LinearLayout�𐶐�����
    	LinearLayout linearLayout = new LinearLayout(this);
    	linearLayout.setOrientation(LinearLayout.VERTICAL);
    	setContentView(linearLayout);

    	// �n�����͕�
    	edit = new EditText(this);
    	edit.setHint("�n���Ȃǂ����");
    	linearLayout.addView(edit, new LinearLayout.LayoutParams(FP, WC));
    	// MapView
    	mapView = new MapView(this, getString(R.string.apikey));	// TODO �l���ꂼ���apikey�������Ń\�������܂��傤�B
    	// mapView�̂����������B���ꂪ����ƕ��ʂ̃}�b�v�Ɠ������o�ł�����Ă��肮��ړ��ł���
    	mapView.setClickable(true);
    	// �Y�[���C���Y�[���A�E�g�{�^����\���B����ƕ֗��B�Ƃ��Ƀ}���`�^�b�`��Ή��̃o�[�W�����ł͂ˁB
    	mapView.setBuiltInZoomControls(true);
    	// ���łɁB���i���Z���X�Ȃ��������A�Y�[���R���g���[�����E�Ɋ񂹂�B(���Ɍ����Ȃ��󔒂���点�Ă���)
    	mapView.getZoomButtonsController().getZoomControls().setPadding(200, 0, 0, 0);
    	// �ŁA����MapView�����C�A�E�g�ɓ˂�����ł���
    	linearLayout.addView(mapView, new LinearLayout.LayoutParams(WC, WC));

    	// ��������LinearLayout��\���ɐݒ�
    	setContentView(linearLayout);

    	// MapView�𑀍삷��MapController�̃C���X�^���X�𐶐�
    	control = mapView.getController();
    	// �Y�[�����Ă����B����Ȃ��ƂȂɂ��N���Ă邩�����ς�킩���B
    	control.setZoom(15);

    	// �e�L�X�g�{�b�N�X�̕ҏW�����m���郊�X�i�[��o�^
    	edit.addTextChangedListener(this);
    }

    @Override
    protected boolean isRouteDisplayed() {
    	return false;
	}

	/**
	 * TextEdit���ҏW���ꂽ��Ăяo�����B
	 * @param s
	 */
	@Override
	public void afterTextChanged(Editable s) {
		// Handler���L�����Z�����čĐݒ�i�ŏI���͂���1�b�҂j
		handler.removeMessages(NOW_EDIT);
		handler.sendEmptyMessageDelayed(NOW_EDIT, 1000);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,int after) {}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {}
	
	private static int NOW_EDIT = 1;

	/**
	 * �Z������͂�����A�������b�҂��ăW�I�R�[�f�B���O����
	 */
 	private Handler handler = new Handler() {
 		@Override
 		public void dispatchMessage(Message msg) {
 			if (msg.what == NOW_EDIT) {
 				try {
			 		// ��������W�I�R�[�f�B���O
 			 		Geocoder geocoder = new Geocoder(getApplicationContext(),Locale.JAPAN);
 					List <Address> address = geocoder.getFromLocationName(edit.getText().toString(), 1);
 					int latitude = (int) (address.get(0).getLatitude()*1E6);
 					int longitude = (int) (address.get(0).getLongitude()*1E6);
 					Log.d("Geo coding","geo coding! done.�@lat:"+latitude+" lon:"+longitude);
 					// �ܓx�o�x����GeoPoint�����
 					GeoPoint geo = new GeoPoint(latitude, longitude);
 					// �n�}�̒��S�_��ݒ�
 					control.setCenter(geo);
 					// Mapview�̍ĕ`��
 					mapView.postInvalidate();
 				} catch (Exception e) {
 					Log.e("Geo coding",e.toString());
 				}
 			} else {
 				super.dispatchMessage(msg);
 			}
 		}
 	};
}