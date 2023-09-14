package lk.live.ccextract;
 
import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.SharedPreferences;
import android.content.Context;
import android.content.ClipboardManager;
import android.content.ClipData;
import android.text.Html;
import android.widget.TextView;
import android.view.View.OnLongClickListener;

public class MainActivity extends Activity { 
    private SharedPreferences sp;
	TextView lbl_cc, lbl_exp, lbl_cvv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		final EditText et_pattern = findViewById(R.id.pattern);
		Button save_pattern = findViewById(R.id.save_pattern);
		Button parse = findViewById(R.id.parse);
		lbl_cc = findViewById(R.id.label_cc);
		lbl_exp = findViewById(R.id.label_exp);
		lbl_cvv = findViewById(R.id.label_cvv);
        sp = getSharedPreferences("cc", Context.MODE_PRIVATE);
		et_pattern.setText(sp.getString("pattern", "{1}|{2}|{3}|{4}"));
		final SharedPreferences.Editor editor = sp.edit();
		save_pattern.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v1){
				editor.putString("pattern", et_pattern.getText().toString());
				editor.apply();
				Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();
			}
		});
		parse.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v1){
				ccfilter();
			}
		});
		OnLongClickListener textViewToClipboard = new OnLongClickListener(){
			@Override
			public boolean onLongClick(View v){
				String txt = ((TextView)v).getText().toString();
				ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				ClipData clip = ClipData.newPlainText("cc", txt);
				clipboard.setPrimaryClip(clip);
				Toast.makeText(getApplicationContext(), "Copied", Toast.LENGTH_SHORT).show();
				return true;
			}
		};
		lbl_cc.setOnLongClickListener(textViewToClipboard);
		lbl_exp.setOnLongClickListener(textViewToClipboard);
		lbl_cvv.setOnLongClickListener(textViewToClipboard);
    }
	@Override
	protected void onResume() {
		super.onResume();
		ccfilter();
	}
	private void ccfilter(){
		ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		if(clipboard.hasPrimaryClip()){
			ClipData clipData = clipboard.getPrimaryClip();
			if(clipData != null && clipData.getItemCount() > 0){
				ClipData.Item item = clipData.getItemAt(0);
				CharSequence clipboardText = item.getText();
				if(clipboardText != null){
					String text = clipboardText.toString();
					String ptrn = sp.getString("pattern", "{1}|{2}|{3}|{4}");
					String[] cc_found = extractor.findcc(text);
					String cc = extractor.parseCc(cc_found, ptrn);
					if(cc == null){
						Toast.makeText(getApplicationContext(), "Error parse text", Toast.LENGTH_LONG).show();
						return;
					}
					ClipData clip = ClipData.newPlainText("cc", cc);
					clipboard.setPrimaryClip(clip);
					lbl_cc.setText(cc_found[0]);
					lbl_exp.setText(cc_found[1]+"/"+cc_found[2]);
					lbl_cvv.setText(cc_found[3]);
					Toast.makeText(getApplicationContext(), "CC Copied to clipboard", Toast.LENGTH_SHORT).show();
				}
				else{
					Toast.makeText(getApplicationContext(), "EMPTY CLIPDATA", Toast.LENGTH_LONG).show();
				}
			}
			else{
				Toast.makeText(getApplicationContext(), "CLIPBOARD DATA ERROR", Toast.LENGTH_LONG).show();
			}
		}
		else{
			Toast.makeText(getApplicationContext(), "NO DATA FOUND IN CLIPBOARD", Toast.LENGTH_LONG).show();
		}
	}
} 
/* MODDED BY MASKED_MODDER*/
