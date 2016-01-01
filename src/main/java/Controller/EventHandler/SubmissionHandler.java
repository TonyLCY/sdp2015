package Controller.EventHandler;

import Controller.Core;
import Controller.Team;
import Controller.DatabaseManager.SubmissionManager;

import java.util.Map;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tenyoku on 2015/12/24.
 */
public class SubmissionHandler extends EventHandler<Team> {
    public SubmissionHandler(EventHandler<? super Team> nextHandler) {
        super(nextHandler);
    }

    private void sendAck(Team team, String submissionID, long timeStamp) {
        try {
            JSONObject msg = new JSONObject();
            msg.append("msg_type", "submit");
            msg.append("status", "success");
            msg.append("submission_id", submissionID);
            msg.append("time_stamp", timeStamp);
            team.send(msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendNak(Team team, long timeStamp) {
        try {
            JSONObject msg = new JSONObject();
            msg.append("msg_type", "submit");
            msg.append("status", "redundant");
            msg.append("time_stamp", timeStamp);
            team.send(msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void forward(String submissionID, String problemID, String language, String sourceCode,
                         long timeStamp, long testDataTimeStamp) {
        try {
            JSONObject msg = new JSONObject();
            msg.append("msg_type", "submit");
            msg.append("submission_id", submissionID);
            msg.append("problem_id", problemID);
            msg.append("language", language);
            msg.append("source_code", sourceCode);
            msg.append("time_stamp", timeStamp);
            msg.append("testdata_time_stamp", testDataTimeStamp);
            Core.getInstance().getScheduler().add(msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handle(Team team, JSONObject msg) {
        try {
            if (msg.getString("msg_type").equals("submit")) {
                SubmissionManager submissionManager = new SubmissionManager();
                String problemID = msg.getString("problem_id");
                String language = msg.getString("language");
                String sourceCode = msg.getString("source_code");
                long timeStamp = System.currentTimeMillis() / 1000; /* TODO: get timer time */
                if (true /*not appeared*/) {
                    Map<String, String> store = new HashMap<String, String>();
                    store.put("problem_id", problemID);
                    store.put("language", language);
                    store.put("team_id", team.getID());
                    store.put("time_stamp", Long.toString(timeStamp));
                    store.put("source_code", sourceCode);
                    String sid = Integer.toString(submissionManager.addEntry(store));

                    forward(sid, problemID, language, sourceCode, timeStamp, Core.getInstance().getProblemByID(problemID).getTestDataTimeStamp());
                    sendAck(team, sid, timeStamp);
                }
                else {
                    sendNak(team, timeStamp);
                }
            }
            else doNext(team, msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
