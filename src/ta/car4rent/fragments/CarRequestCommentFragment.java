package ta.car4rent.fragments;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import ta.car4rent.R;
import ta.car4rent.activities.MainActivity;
import ta.car4rent.configures.ConfigureData;
import ta.car4rent.objects.CommentArrayRequestCarAdapter;
import ta.car4rent.utils.StaticFunction;
import ta.car4rent.webservices.OnPostJsonListener;
import ta.car4rent.webservices.ServiceManagerPostCarRequestComment;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class CarRequestCommentFragment extends Fragment implements
		OnClickListener {
	View view = null;
	// comment

	Button btnSendComment;
	EditText edtContentComment;

	private CommentArrayRequestCarAdapter commentAdapter;
	private ListView listComment;

	int carRequestId = 0;

	public CarRequestCommentFragment() {
	}

	public void setCarRequestCommentId(int id) {
		carRequestId = id;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		MainActivity.Instance.showActionFilterSpinner(false);
		
		if (view == null) {
			view = inflater.inflate(R.layout.fragment_car_request_comment,
					container, false);

		} else {
			// If we are returning from a configuration change:
			// "view" is still attached to the previous view hierarchy
			// so we need to remove it and re-attach it to the current one
			ViewGroup parent = (ViewGroup) view.getParent();
			parent.removeView(view);
		}
		

		// findview and setOnclick
		edtContentComment = (EditText) view.findViewById(R.id.edtCommentRequest);
		btnSendComment = (Button) view.findViewById(R.id.btnSendCommentRequest);
		listComment = (ListView) view.findViewById(R.id.listCommentRequest);

		btnSendComment.setOnClickListener(this);

		// comment adapter
		commentAdapter = new CommentArrayRequestCarAdapter(ConfigureData.activityMain, R.layout.row_coment_request_car);
		listComment.setAdapter(commentAdapter);
		
		return view;
	}

	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		MainActivity.Instance.showActionFilterSpinner(false);
		super.onResume();
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		commentAdapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View view) {
		StaticFunction.hideKeyboard(ConfigureData.activityMain);
		switch (view.getId()) {
		case R.id.btnSendCommentRequest:
			final String contentComent = edtContentComment.getText().toString();
			final JSONObject commentObject = new JSONObject();

			// reset edt
			edtContentComment.setText("");
			try {
				commentObject.put("requestId", carRequestId);
				commentObject.put("content", contentComent);
				ServiceManagerPostCarRequestComment servicePostComment = new ServiceManagerPostCarRequestComment();
				servicePostComment.postCarRequestComment(commentObject);
				servicePostComment
						.addOnPostJsonListener(new OnPostJsonListener() {

							@Override
							public void onPostJsonFail(String response) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onPostJsonCompleted(String response) {
								try {
									commentObject.put("date", getCurrenttDateTimeFormated());
									commentObject.put("comment", contentComent);
									commentObject.put("user",
											ConfigureData.userAccount
													.get("name"));
									commentObject.put("image",
											ConfigureData.userAccount
													.get("image"));
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								commentAdapter.add(commentObject);
								listComment.setSelection(commentAdapter
										.getCount() - 1);

							}
						});
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

			break;

		default:
			break;
		}

	}
	
	/**
	 * Get current date as String
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getCurrenttDateTimeFormated() {
		final Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm dd/MM/yyyy");
		return sdf.format(c.getTime());
	}


}
