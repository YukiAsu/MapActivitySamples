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
 * MapViewいじりサンプル！<br>
 * 今できること：<br>
 * ・上端のテキストボックスに入力した内容をリアルタイム（1秒ごとに）ジオコーディングして、その地点をセンタリング<br>
 * ・ズームコントロールを右にちょっとずらす。
 * @author YukiAsu
 */
public class GeoCodingMapActivity extends MapActivity implements TextWatcher{

	/** 
	 * 画面上部の入力用ボックス
	 */
	private EditText edit;
	/**
	 * 地図
	 */
	private MapView mapView;
	/**
	 * マップコントローラ。コレを介して中心点移動やズームを行う。
	 */
	private MapController control;

	// レイアウト用の定数。長いので。
    private final int FP = ViewGroup.LayoutParams.FILL_PARENT; 
    private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

    	// 地名入力部とMapViewを備えたLinearLayoutを生成する
    	LinearLayout linearLayout = new LinearLayout(this);
    	linearLayout.setOrientation(LinearLayout.VERTICAL);
    	setContentView(linearLayout);

    	// 地名入力部
    	edit = new EditText(this);
    	edit.setHint("地名などを入力");
    	linearLayout.addView(edit, new LinearLayout.LayoutParams(FP, WC));
    	// MapView
    	mapView = new MapView(this, getString(R.string.apikey));	// TODO 人それぞれのapikeyがあるんでソレを入れましょう。
    	// mapViewのおさわりを許可。これがあると普通のマップと同じ感覚でさわってぐりぐり移動できる
    	mapView.setClickable(true);
    	// ズームインズームアウトボタンを表示。あると便利。とくにマルチタッチ非対応のバージョンではね。
    	mapView.setBuiltInZoomControls(true);
    	// ついでに。ややナンセンスなやり方だが、ズームコントロールを右に寄せる。(左に見えない空白を作らせている)
    	mapView.getZoomButtonsController().getZoomControls().setPadding(200, 0, 0, 0);
    	// で、そのMapViewをレイアウトに突っ込んでおく
    	linearLayout.addView(mapView, new LinearLayout.LayoutParams(WC, WC));

    	// 生成したLinearLayoutを表示に設定
    	setContentView(linearLayout);

    	// MapViewを操作するMapControllerのインスタンスを生成
    	control = mapView.getController();
    	// ズームしておく。じゃないとなにが起きてるかさっぱりわからん。
    	control.setZoom(15);

    	// テキストボックスの編集を検知するリスナーを登録
    	edit.addTextChangedListener(this);
    }

    @Override
    protected boolean isRouteDisplayed() {
    	return false;
	}

	/**
	 * TextEditが編集されたら呼び出される。
	 * @param s
	 */
	@Override
	public void afterTextChanged(Editable s) {
		// Handlerをキャンセルして再設定（最終入力から1秒待つ）
		handler.removeMessages(NOW_EDIT);
		handler.sendEmptyMessageDelayed(NOW_EDIT, 1000);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,int after) {}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {}
	
	private static int NOW_EDIT = 1;

	/**
	 * 住所を入力したら、完了後一秒待ってジオコーディングする
	 */
 	private Handler handler = new Handler() {
 		@Override
 		public void dispatchMessage(Message msg) {
 			if (msg.what == NOW_EDIT) {
 				try {
			 		// ここからジオコーディング
 			 		Geocoder geocoder = new Geocoder(getApplicationContext(),Locale.JAPAN);
 					List <Address> address = geocoder.getFromLocationName(edit.getText().toString(), 1);
 					int latitude = (int) (address.get(0).getLatitude()*1E6);
 					int longitude = (int) (address.get(0).getLongitude()*1E6);
 					Log.d("Geo coding","geo coding! done.　lat:"+latitude+" lon:"+longitude);
 					// 緯度経度からGeoPointを作る
 					GeoPoint geo = new GeoPoint(latitude, longitude);
 					// 地図の中心点を設定
 					control.setCenter(geo);
 					// Mapviewの再描画
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