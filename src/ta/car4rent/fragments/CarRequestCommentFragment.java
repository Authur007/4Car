package ta.car4rent.fragments;

import org.json.JSONException;
import org.json.JSONObject;

import ta.car4rent.R;
import ta.car4rent.configures.ConfigureData;
import ta.car4rent.objects.ComentArrayRequestCarAdapter;
import ta.car4rent.utils.StaticFunction;
import ta.car4rent.webservices.OnPostJsonListener;
import ta.car4rent.webservices.ServiceManagerPostCarRequestComment;
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

	private ComentArrayRequestCarAdapter commentAdapter;
	private ListView listComent;

	int carRequestId = 0;

	public CarRequestCommentFragment() {
	}

	public void setCarRequestCommentId(int id) {
		carRequestId = id;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

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
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		// findview and setOnclick
		edtContentComment = (EditText) view.findViewById(R.id.edtComentRequest);
		btnSendComment = (Button) view.findViewById(R.id.btnSendComentRequest);
		listComent = (ListView) view.findViewById(R.id.listComentRequest);

		btnSendComment.setOnClickListener(this);

		// commnent adapter
		commentAdapter = new ComentArrayRequestCarAdapter(
				ConfigureData.activityMain, R.layout.row_coment_request_car);
		listComent.setAdapter(commentAdapter);
	}

	@Override
	public void onClick(View view) {
		StaticFunction.hideKeyboard(ConfigureData.activityMain);
		switch (view.getId()) {
		case R.id.btnSendComentRequest:
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
									commentObject.put("date", "null");
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
								listComent.setSelection(commentAdapter
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

}
