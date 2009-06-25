package edu.nrao.dss.client;

import java.lang.Double;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;

import edu.nrao.dss.client.model.MinimumObservingEfficiency;
import edu.nrao.dss.client.model.Receiver;

class ColumnDefinition {
    // Use this enumeration to get compile-time checking of column IDs.
    public static final String AUTHORIZED        = "authorized";
    public static final String BETWEEN           = "between";
    public static final String BACKUP            = "backup";
    public static final String CODE              = "pcode";
    public static final String COMPLETE          = "complete";
    public static final String COORD_MODE        = "coord_mode";
    public static final String ENABLED           = "enabled";
    public static final String FREQ              = "freq";
    public static final String GRADE             = "grade";
    public static final String ID                = "id";
    public static final String NAME              = "name";
    public static final String ORIG_ID           = "orig_ID";
    public static final String RECEIVER          = "receiver";
    public static final String PSC_TIME          = "PSC_time";
    public static final String REQ_MAX           = "req_max";
    public static final String REQ_MIN           = "req_min";
    public static final String SCIENCE           = "science";
    public static final String SEM_TIME          = "sem_time";
    public static final String SOURCE            = "source";
    public static final String SOURCE_H          = "source_h";
    public static final String SOURCE_V          = "source_v";
    public static final String TOTAL_TIME        = "total_time";
    public static final String TYPE              = "type";

    public ColumnDefinition() {
        for (ColumnType column : columns) {
            columnsMap.put(column.getId(), column);
        }
    }
    
    /** Return a ColumnModel suitable for defining a grid. */
    public ColumnModel getColumnModel(ColumnConfig column) {
        ArrayList<ColumnConfig> columns = new ArrayList<ColumnConfig>();
        if (column != null) {
            columns.add(column);
        }
        columns.addAll(Arrays.asList((ColumnConfig[]) this.columns));

        return new ColumnModel(columns);
    }    		


    /** Return the master list of all know columns. */
    public List<String> getAllFieldNames() {
        ArrayList<String> result = new ArrayList<String>();
        for (ColumnType column : columns) {
            result.add(column.getHeader());
        }
        return result;
    }

    public List<String> getAllFieldIds() {
        ArrayList<String> result = new ArrayList<String>();
        for (ColumnType column : columns) {
            result.add(column.getId());
        }
        return result;
    }

    public boolean hasColumn(String id) {
        return columnsMap.containsKey(id);
    }

    public ColumnType getColumn(String id) {
        return columnsMap.get(id);
    }

    public Object getValue(String id, RowType row, Map<String, Object> model) {
        return getColumn(id).getValue(row, model);
    }

    public boolean hasColumnDefault(String id) {
    	return getColumn(id).hasColumnDefault();
    }
    
    @SuppressWarnings("unchecked")
    public Class getClasz(String id) {
        return columnsMap.get(id).clasz;
    }

    @SuppressWarnings("unchecked")
    public Field getField(String id) {
        return getColumn(id).getField();
    }

    private final CalculatedField receivers = new CalculatedField() {
        public Object calculate(RowType row, Map<String, Object> model) {
        	double frequency;
        	if (row == null) {
        		frequency = Double.parseDouble(model.get(FREQ).toString());
        	}
        	else {
        		frequency = ((Double)(row.getValue(FREQ, model))).doubleValue();
        	}
            return Receiver.deriveReceiver(frequency);
        }
    };
    
    private final CalculatedField obsEffLimit = new CalculatedField() {
        public Object calculate(RowType row, Map<String, Object> model) {
        	double frequency;
        	if (row == null) {
        		frequency = ((Double)(model.get(FREQ))).doubleValue();
        	}
        	else {
        		frequency = ((Double)(row.getValue(FREQ, model))).doubleValue();
        	}
        	MinimumObservingEfficiency moe = new MinimumObservingEfficiency();
            return moe.efficiency(frequency);
        }
    };

    /*
    private final CalculatedField atmosStLimit = new CalculatedField() {
        public Object calculate(RowType row, Map<String, Object> model) {
            return row.getValue(FREQ, model);
        }
    };

    private final CalculatedField trErrLimit = new CalculatedField() {
        public Object calculate(RowType row, Map<String, Object> model) {
            return row.getValue(FREQ, model);
        }
    };

    private final CalculatedField minEffTsys = new CalculatedField() {
        public Object calculate(RowType row, Map<String, Object> model) {
            return row.getValue(FREQ, model);
        }
    };
    */

    private final CalculatedField haLimit = new CalculatedField() {
        public Object calculate(RowType row, Map<String, Object> model) {
            return row.getValue(FREQ, model);
        }
    };

    // The defaults found in this table are constant across all possible row types
    // and are also defaulted on the server side.  Therefore those sessions having
    // these defaults will not forward them to the server and database.
    private final ColumnType[] columns = new ColumnType[] {
            new ColumnType(CODE,           "Proj Code",      100, PCodeField.class,           null),
            new ColumnType(NAME,           "Name",           175, String.class,               null),
            new ColumnType(SOURCE,         "Source",         100, String.class,               null),
            new ColumnType(ORIG_ID,        "Orig ID",         50, Integer.class,              null),
            new ColumnType(ID,             "ID",              50, Integer.class,              null),
            new ColumnType(TYPE,           "Type",            60, STypeField.class,           null),
            new ColumnType(SCIENCE,        "Science",         75, ScienceField.class,         null),
            new ColumnType(PSC_TIME,       "PSC Time",        60, Double.class,               null),
            new ColumnType(TOTAL_TIME,     "Total Time",      60, Double.class,               null),
            new ColumnType(SEM_TIME,       "Semester Time",  100, Double.class,               null),
            new ColumnType(GRADE,          "Grade",           50, GradeField.class,           null),
            new ColumnType(FREQ,           "Freq",            50, Double.class,               null),
            new ColumnType(RECEIVER,       "Receiver(s)",    100, String.class,               receivers),
            new ColumnType(REQ_MIN,        "Req Min",         60, Double.class,               null),
            new ColumnType(REQ_MAX,        "Req Max",         60, Double.class,               null),
            new ColumnType(COORD_MODE,     "Coord Mode",      75, CoordModeField.class,       null),
            new ColumnType(SOURCE_H,       "Source H",        75, TimeField.class,            null),
            new ColumnType(SOURCE_V,       "Source V",        75, DegreeField.class,          null),
            new ColumnType(BETWEEN,        "Between",         60, Double.class,               null),
            new ColumnType(AUTHORIZED,     "Authorized",      75, Boolean.class,              null),
            new ColumnType(ENABLED,        "Enabled",         75, Boolean.class,              null),
            new ColumnType(COMPLETE,       "Complete",        75, Boolean.class,              null),
            new ColumnType(BACKUP,         "Backup",          50, Boolean.class,              null),
    };

    private final HashMap<String, ColumnType> columnsMap = new HashMap<String, ColumnType>();
}
