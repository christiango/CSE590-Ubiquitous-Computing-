package go.christian.steptracker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class StepCountActivity extends AppCompatActivity implements SensorEventListener {

  private StepCounter _stepCounter = StepCounterFactory.GetStepCounter();

  private TextView _stepCountTextView;

  private int _stepGoal = 10000;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_step_count);

    SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    Sensor accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    sensorManager.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_UI);

    _stepCountTextView = findViewById(R.id.stepCount);

    updateUI();
  }

  @Override
  public void onSensorChanged(SensorEvent event) {
    if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
      _stepCounter.addData(event.values);
      updateUI();
    }
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {}

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.step_counter_settings, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle item selection
    switch (item.getItemId()) {
      case R.id.set_step_goals:
        showStepCounterDialog();
        return true;
      case R.id.navigate_diagnostics:
        Intent intent = new Intent(this, DiagnosticsActivity.class);
        startActivity(intent);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private void showStepCounterDialog() {
    final CharSequence[] stepGoalOptions =
        new CharSequence[] {"10", "20", "100", "1000", "10000", "20000"};

    AlertDialog.Builder builder = new AlertDialog.Builder(this);

    builder
        .setTitle("Set Step Goal")
        .setItems(
            stepGoalOptions,
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {
                setNewStepGoal(Integer.parseInt(stepGoalOptions[which].toString()));
              }
            });

    AlertDialog dialog = builder.create();

    dialog.show();
  }

  private void setNewStepGoal(int newGoal) {
    _stepGoal = newGoal;
  }

  private void updateUI() {
    int stepCount = _stepCounter.getStepCount();

    ImageView image = findViewById(R.id.progressImage);
    image.setImageDrawable(new PacmanDrawable(stepCount* 1.0 / _stepGoal));

    _stepCountTextView.setText(_stepCounter.getStepCount() + "/" + _stepGoal);
  }
}
