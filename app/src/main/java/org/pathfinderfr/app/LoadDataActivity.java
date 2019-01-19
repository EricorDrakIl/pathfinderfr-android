package org.pathfinderfr.app;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.pathfinderfr.R;
import org.pathfinderfr.app.data.LoadDataTask;
import org.pathfinderfr.app.database.entity.DBEntity;
import org.pathfinderfr.app.database.entity.DBEntityFactory;
import org.pathfinderfr.app.database.entity.FeatFactory;
import org.pathfinderfr.app.database.entity.RaceFactory;
import org.pathfinderfr.app.database.entity.SkillFactory;
import org.pathfinderfr.app.database.entity.SpellFactory;

public class LoadDataActivity extends AppCompatActivity implements LoadDataTask.IDataUI {

    private static final String[] SOURCES = new String[]{
            "https://raw.githubusercontent.com/SvenWerlen/pathfinderfr-data/master/data/competences.yml",
            "https://raw.githubusercontent.com/SvenWerlen/pathfinderfr-data/master/data/dons.yml",
            "https://raw.githubusercontent.com/SvenWerlen/pathfinderfr-data/master/data/spells.yml",
            "https://raw.githubusercontent.com/SvenWerlen/pathfinderfr-data/master/data/races.yml"};

    private static final String[] SOURCES_TEST = new String[]{
            "https://raw.githubusercontent.com/SvenWerlen/pathfinderfr-data/Test/datamigration/data/competences.yml",
            "https://raw.githubusercontent.com/SvenWerlen/pathfinderfr-data/Test/datamigration/data/dons.yml",
            "https://raw.githubusercontent.com/SvenWerlen/pathfinderfr-data/Test/datamigration/data/spells.yml"};

    private static final String[] SOURCES_NEW = new String[]{
            "https://raw.githubusercontent.com/SvenWerlen/pathfinderfr-data/Feature/sources/data/competences.yml",
            "https://raw.githubusercontent.com/SvenWerlen/pathfinderfr-data/Feature/sources/data/dons.yml",
            "https://raw.githubusercontent.com/SvenWerlen/pathfinderfr-data/Feature/sources/data/spells.yml"};


    private static final String[] SOURCES_NAMES = new String[]{"Compétences", "Dons", "Sorts", "Races"};
    private LoadDataTask loadTaskInProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_data);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button button = findViewById(R.id.loaddataButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loadTaskInProgress == null) {
                    // disable buttons to force user to stay
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    Button button = findViewById(R.id.loaddataButton);
                    button.setText(getResources().getString(R.string.loaddata_stop));
                    findViewById(R.id.loaddataProgressBar).setVisibility(View.VISIBLE);
                    findViewById(R.id.loaddataInfos).setVisibility(View.VISIBLE);
                    boolean deleteOrpheans = ((CheckBox)findViewById(R.id.loaddataDeleteOrpheans)).isChecked();

                    Pair<String, DBEntityFactory> source0 = new Pair(SOURCES[0], SkillFactory.getInstance());
                    Pair<String, DBEntityFactory> source1 = new Pair(SOURCES[1], FeatFactory.getInstance());
                    Pair<String, DBEntityFactory> source2 = new Pair(SOURCES[2], SpellFactory.getInstance());
                    Pair<String, DBEntityFactory> source3 = new Pair(SOURCES[3], RaceFactory.getInstance());
                    loadTaskInProgress = new LoadDataTask(LoadDataActivity.this, deleteOrpheans);
                    //loadTaskInProgress.execute(source0,source1,source2,source3);
                    loadTaskInProgress.execute(source3);

                } else {
                    Button button = findViewById(R.id.loaddataButton);
                    button.setText(getResources().getString(R.string.loaddata_stopping));
                    button.setEnabled(false);
                    if(loadTaskInProgress != null) {
                        loadTaskInProgress.cancel(false);
                    }

                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (loadTaskInProgress == null) {
            super.onBackPressed();
        } else {
            Snackbar.make(findViewById(android.R.id.content),
                    getResources().getString(R.string.loaddata_pleasewait), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    @Override
    public void onProgressUpdate(LoadDataTask.UpdateStatus... progresses) {
        int totalProgress = 0;
        for(LoadDataTask.UpdateStatus p : progresses) {
            if(p != null) {
                totalProgress += (int) ((p.getCountProcessed() / (float) p.getCountTotal()) * 100);
            }
        }

        // update information
        boolean completed = true;
        String text = "";
        for(int i=0; i<progresses.length; i++) {
            String status;
            if(progresses[i] == null) {
                status = getResources().getString(R.string.loaddata_waiting);
            } else if(progresses[i].getCountTotal() == 0) {
                status = getResources().getString(R.string.loaddata_downloading);
                completed = completed && progresses[i].hasEnded();
            } else {
                String done = String.valueOf(progresses[i].getCountProcessed());
                String total = String.valueOf(progresses[i].getCountTotal());
                String percentage = String.valueOf((int)((progresses[i].getCountProcessed() / (float)progresses[i].getCountTotal()) * 100));
                String template = getResources().getString(R.string.loaddata_inprogress);
                status = String.format(template,done,total,percentage + '%');
                completed = completed && progresses[i].hasEnded();
            }
            text += "<b>" + SOURCES_NAMES[i] + "</b>: " + status + "<br/>";
        }
        if(completed) {
            text += "<br/>" + favoriteMigrationText(progresses);
        }

        final int progress = totalProgress / progresses.length;
        final String message = text;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((ProgressBar) findViewById(R.id.loaddataProgressBar)).setProgress(progress);
                ((TextView)findViewById(R.id.loaddataInfos)).setText(Html.fromHtml(message));
            }
        });
    }

    @Override
    public void onProgressCompleted(Integer... counts) {
        // change status
        loadTaskInProgress = null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String buttonText = getResources().getString(R.string.loaddata_start);
        final int progress = 100;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((Button)findViewById(R.id.loaddataButton)).setText(buttonText);
                ((Button)findViewById(R.id.loaddataButton)).setEnabled(true);
                ((ProgressBar) findViewById(R.id.loaddataProgressBar)).setProgress(progress);
            }
        });
    }

    /**
     * Generates debugging information about the migration of favorites
     * @param progresses the progress information (when completed)
     * @return html text to be displayed
     */
    private String favoriteMigrationText(LoadDataTask.UpdateStatus... progresses) {

        String text = "";
        for(LoadDataTask.UpdateStatus status : progresses) {
            if(status == null) {
                continue;
            }
            for(Pair<DBEntity,Integer> fav: status.getFavoriteStatus()) {
                boolean error = false;
                String statusText = null;
                switch (fav.second) {
                    case LoadDataTask.UpdateStatus.STATUS_PENDING:
                        statusText = getResources().getString(R.string.loaddata_status_pending);
                        break;
                    case LoadDataTask.UpdateStatus.STATUS_NOTCHANGED:
                        statusText = getResources().getString(R.string.loaddata_status_notchanged);
                        break;
                    case LoadDataTask.UpdateStatus.STATUS_CHANGED:
                        statusText = getResources().getString(R.string.loaddata_status_changed);
                        break;
                    case LoadDataTask.UpdateStatus.STATUS_NOTFOUND:
                        statusText = getResources().getString(R.string.loaddata_status_notfound);
                        error = true;
                        break;
                    case LoadDataTask.UpdateStatus.STATUS_DELETED:
                        statusText = getResources().getString(R.string.loaddata_status_deleted);
                        error = true;
                        break;
                    default:
                        statusText = getResources().getString(R.string.loaddata_status_error);
                        error = true;
                        break;
                }
                if (error) {
                    text += String.format("<b>%s</b>: <span style=\"color:#cc0000\">%s</span><br/>",
                            fav.first.getName(), statusText);
                } else {
                    text += String.format("<b>%s</b>: <span style=\"color:#006600\">%s</span><br/>",
                            fav.first.getName(), statusText);
                }
            }
        }

        return text;
    }



}
