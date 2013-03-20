package de.azapps.mirakel;


import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

public class TaskActivity extends Activity {
	private static final String TAG = "TaskActivity";
	protected long id;
	protected Task task;
	private TasksDataSource datasource;
	protected TextView Task_name;
	protected CheckBox Task_done;
	protected TextView Task_prio;
	protected TextView Task_content;
	protected TextView Task_due;

	protected TaskActivity main;
	protected NumberPicker picker;
	protected EditText input;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main = this;
		setContentView(R.layout.activity_task);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			id = extras.getLong("id");
		} else {
			id = -1;
		}
		Log.v(TAG, "Taskid " + id);

		Task_name = (TextView) findViewById(R.id.task_name);
		Task_done = (CheckBox) findViewById(R.id.task_done);
		Task_prio = (TextView) findViewById(R.id.task_prio);
		Task_content = (TextView) findViewById(R.id.task_content);
		Task_due=(TextView)findViewById(R.id.task_due);
		datasource = new TasksDataSource(this);
		datasource.open();
		task = datasource.getTask(id);
		Task_name.setText(task.getName());
		Task_content.setText(task.getContent().trim().length() == 0 ? this
				.getString(R.string.task_no_content) : task.getContent());
		Task_done.setChecked(task.isDone());
		//String due=;
		Task_due.setText(task.getDue().compareTo(new GregorianCalendar(1970, 1, 1))<0?this.getString(R.string.task_no_due):(task.getDue().get(Calendar.DAY_OF_MONTH)+"."+ (task.getDue().get(Calendar.MONTH)+1)+"."+task.getDue().get(Calendar.YEAR)));
		set_prio(Task_prio, task);
		Task_due.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				GregorianCalendar due =(task.getDue().compareTo(new GregorianCalendar())<0?new GregorianCalendar():task.getDue());
				(new DatePickerDialog(main, new OnDateSetListener() {
					
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear,
							int dayOfMonth) {
						task.setDue(new GregorianCalendar(year, monthOfYear, dayOfMonth));
						datasource.saveTask(task);	
						Task_due.setText(dayOfMonth+"."+(monthOfYear+1)+"."+year);
					}
				}, due.get(Calendar.YEAR),due.get(Calendar.MONTH),due.get(Calendar.DAY_OF_MONTH))).show();
				
			}
		});

		Task_name.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				input = new EditText(main);
				input.setText(task.getName());
				input.setTag(main);
				new AlertDialog.Builder(main)
						.setTitle(
								main.getString(R.string.task_change_name_title))
						.setMessage(
								main.getString(R.string.task_change_name_cont))
						.setView(input)
						.setPositiveButton(main.getString(R.string.OK),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										task.setName(input.getText().toString());
										datasource.saveTask(task);
										Task_name.setText(task.getName());
									}
								})
						.setNegativeButton(main.getString(R.string.Cancel),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										// Do nothing.
									}
								}).show();
			}
		});
		Task_content.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				input = new EditText(main);
				input.setText(task.getContent());
				input.setTag(main);
				input.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
				new AlertDialog.Builder(main)
						.setTitle(
								main.getString(R.string.task_change_content_title))
						.setMessage(
								main.getString(R.string.task_change_content_cont))
						.setView(input)
						.setPositiveButton(main.getString(R.string.OK),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										task.setContent(input.getText()
												.toString());
										datasource.saveTask(task);
										Task_content.setText(task.getContent());
									}
								})
						.setNegativeButton(main.getString(R.string.Cancel),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										// Do nothing.
									}
								}).show();
			}
		});

		Task_done.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				task.setDone(isChecked);
				datasource.saveTask(task);
			}
		});
		Task_prio.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				picker = new NumberPicker(main);
				picker.setMaxValue(4);
				picker.setMinValue(0);
				String[] t = { "-2", "-1", "0", "1", "2" };
				picker.setDisplayedValues(t);
				picker.setWrapSelectorWheel(false);
				picker.setValue(task.getPriority() + 2);
				new AlertDialog.Builder(main)
						.setTitle(
								main.getString(R.string.task_change_prio_title))
						.setMessage(
								main.getString(R.string.task_change_prio_cont))
						.setView(picker)
						.setPositiveButton(main.getString(R.string.OK),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										task.setPriority((picker.getValue() - 2));
										datasource.saveTask(task);
										TaskActivity.set_prio(Task_prio, task);
									}

								})
						.setNegativeButton(main.getString(R.string.Cancel),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										// Do nothing.
									}
								}).show();

			}
		});
		Map<SwipeListener.Direction, SwipeCommand> commands = new HashMap<SwipeListener.Direction, SwipeCommand>();
		commands.put(SwipeListener.Direction.LEFT, new SwipeCommand() {
			@Override
			public void runCommand(View v, MotionEvent event) {
				finish();
			}
		});
		((LinearLayout) this.findViewById(R.id.task_details))
				.setOnTouchListener(new SwipeListener(true,commands));

	} // Log.e(TAG,task.getContent().trim().length()+"");

	protected static void set_prio(TextView Task_prio, Task task) {
		Task_prio.setText("" + task.getPriority());
		Task_prio
				.setBackgroundColor(Mirakel.PRIO_COLOR[task.getPriority() + 2]);

	}

	@Override
	public void onPause() {
		datasource.close();
		super.onPause();
	}

	@Override
	public void onStart() {
		super.onStart();
		datasource.open();
	}

	@Override
	public void onRestart() {
		super.onRestart();
		datasource.open();
	}

	@Override
	public void onResume() {
		super.onResume();
		datasource.open();
	}

	@Override
	public void onStop() {
		datasource.close();
		super.onStop();
	}

	@Override
	public void onDestroy() {
		datasource.close();
		super.onDestroy();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_delete:
			new AlertDialog.Builder(this)
					.setTitle(this.getString(R.string.task_delete_title))
					.setMessage(this.getString(R.string.task_delete_content))
					.setPositiveButton(this.getString(R.string.Yes),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									datasource.deleteTask(task);
									finish();
								}
							})
					.setNegativeButton(this.getString(R.string.no),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// do nothing
								}
							}).show();
			return true;
		case android.R.id.home:
			finish();
		default:
			return super.onOptionsItemSelected(item);

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_task, menu);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		return true;
	}

}