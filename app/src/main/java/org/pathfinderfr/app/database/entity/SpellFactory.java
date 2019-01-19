package org.pathfinderfr.app.database.entity;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;

import org.pathfinderfr.app.util.ConfigurationUtil;
import org.pathfinderfr.app.util.StringUtil;

import java.util.Map;

public class SpellFactory extends DBEntityFactory {

    public static final String FACTORY_ID        = "SPELLS";

    private static final String TABLENAME         = "spells";
    private static final String COLUMN_SCHOOL     = "school";
    private static final String COLUMN_LEVEL      = "level";
    private static final String COLUMN_CASTING    = "castingtime";
    private static final String COLUMN_COMPONENTS = "components";
    private static final String COLUMN_RANGE      = "range";
    private static final String COLUMN_TARGET     = "target";
    private static final String COLUMN_DURATION   = "duration";
    private static final String COLUMN_SAVING     = "savingthrow";
    private static final String COLUMN_SPELL_RES  = "spellresistance";

    private static final String YAML_NAME       = "Nom";
    private static final String YAML_DESC       = "Description";
    private static final String YAML_REFERENCE  = "Référence";
    private static final String YAML_SOURCE     = "Source";
    private static final String YAML_SCHOOL     = "École";
    private static final String YAML_LEVEL      = "Niveau";
    private static final String YAML_CASTING    = "Temps d’incantation";
    private static final String YAML_COMPONENTS = "Composantes";
    private static final String YAML_RANGE      = "Portée";
    private static final String YAML_TARGET     = "Cible ou zone d'effet";
    private static final String YAML_DURATION   = "Durée";
    private static final String YAML_SAVING     = "Jet de sauvegarde";
    private static final String YAML_SPELL_RES  = "Résistance à la magie";


    private static SpellFactory instance;

    private SpellFactory() {
    }

    /**
     * @return then unique instance of that factory
     */
    public static synchronized SpellFactory getInstance() {
        if (instance == null) {
            instance = new SpellFactory();
        }
        return instance;
    }

    @Override
    public String getFactoryId() {
        return FACTORY_ID;
    }

    @Override
    public String getTableName() {
        return SpellFactory.TABLENAME;
    }




    @Override
    public String getQueryCreateTable() {
        String query = String.format( "CREATE TABLE IF NOT EXISTS %s (" +
                        "%s integer PRIMARY key, " +
                        "%s text, %s text, %s text, %s text," +
                        "%s text, %s text, %s text, %s text, %s text," +
                        "%s text, %s text, %s text, %s text" +
                        ")",
                TABLENAME, COLUMN_ID,
                COLUMN_NAME, COLUMN_DESC, COLUMN_REFERENCE, COLUMN_SOURCE,
                COLUMN_SCHOOL, COLUMN_LEVEL, COLUMN_CASTING,  COLUMN_COMPONENTS, COLUMN_RANGE,
                COLUMN_TARGET, COLUMN_DURATION, COLUMN_SAVING, COLUMN_SPELL_RES);
        return query;
    }

    /**
     * @return the query to fetch all entities (including fields required for filtering)
     */
    public String getQueryFetchAll() {
        return String.format("SELECT %s,%s,%s,%s FROM %s ORDER BY %s COLLATE UNICODE",
                COLUMN_ID, COLUMN_NAME, COLUMN_SCHOOL, COLUMN_LEVEL, getTableName(), COLUMN_NAME);
    }

    /**
     * @return the query to fetch all entities (including fields required for filtering)
     */
    public String getQueryFetchAll(String... sources) {
        String sourceList = StringUtil.listToString( sources,',','\'');
        return String.format("SELECT %s,%s,%s,%s FROM %s WHERE %s IN (%s) ORDER BY %s COLLATE UNICODE",
                COLUMN_ID, COLUMN_NAME, COLUMN_SCHOOL, COLUMN_LEVEL, getTableName(), COLUMN_SOURCE, sourceList, COLUMN_NAME);
    }

    @Override
    public ContentValues generateContentValuesFromEntity(@NonNull DBEntity entity) {
        if (!(entity instanceof Spell)) {
            return null;
        }
        Spell spell = (Spell) entity;
        ContentValues contentValues = new ContentValues();
        contentValues.put(SpellFactory.COLUMN_NAME, spell.getName());
        contentValues.put(SpellFactory.COLUMN_DESC, spell.getDescription());
        contentValues.put(SpellFactory.COLUMN_REFERENCE, spell.getReference());
        contentValues.put(SpellFactory.COLUMN_SOURCE, spell.getSource());
        contentValues.put(SpellFactory.COLUMN_SCHOOL, spell.getSchool());
        contentValues.put(SpellFactory.COLUMN_LEVEL, spell.getLevel());
        contentValues.put(SpellFactory.COLUMN_CASTING, spell.getCastingTime());
        contentValues.put(SpellFactory.COLUMN_COMPONENTS, spell.getComponents());
        contentValues.put(SpellFactory.COLUMN_RANGE, spell.getRange());
        contentValues.put(SpellFactory.COLUMN_TARGET, spell.getTarget());
        contentValues.put(SpellFactory.COLUMN_DURATION, spell.getDuration());
        contentValues.put(SpellFactory.COLUMN_SAVING, spell.getSavingThrow());
        contentValues.put(SpellFactory.COLUMN_SPELL_RES, spell.getSpellResistance());
        return contentValues;
    }

    private static String extractValue(@NonNull final Cursor resource, String columnName) {
        if(resource.getColumnIndex(columnName)>=0) {
            return resource.getString(resource.getColumnIndex(columnName));
        } else {
            return null;
        }
    }

    @Override
    public DBEntity generateEntity(@NonNull final Cursor resource) {
        Spell spell = new Spell();
        
        spell.setId(resource.getLong(resource.getColumnIndex(SpellFactory.COLUMN_ID)));
        spell.setName(extractValue(resource,SpellFactory.COLUMN_NAME));
        spell.setDescription(extractValue(resource,SpellFactory.COLUMN_DESC));
        spell.setReference(extractValue(resource,SpellFactory.COLUMN_REFERENCE));
        spell.setSource(extractValue(resource,SpellFactory.COLUMN_SOURCE));
        spell.setSchool(extractValue(resource,SpellFactory.COLUMN_SCHOOL));
        spell.setLevel(extractValue(resource,SpellFactory.COLUMN_LEVEL));
        spell.setCastingTime(extractValue(resource,SpellFactory.COLUMN_CASTING));
        spell.setComponents(extractValue(resource,SpellFactory.COLUMN_COMPONENTS));
        spell.setRange(extractValue(resource,SpellFactory.COLUMN_RANGE));
        spell.setTarget(extractValue(resource,SpellFactory.COLUMN_TARGET));
        spell.setDuration(extractValue(resource,SpellFactory.COLUMN_DURATION));
        spell.setSavingThrow(extractValue(resource,SpellFactory.COLUMN_SAVING));
        spell.setSpellResistance(extractValue(resource,SpellFactory.COLUMN_SPELL_RES));
        return spell;
    }

    @Override
    public DBEntity generateEntity(@NonNull Map<String, Object> attributes) {
        Spell spell = new Spell();
        spell.setName((String)attributes.get(YAML_NAME));
        spell.setDescription((String)attributes.get(YAML_DESC));
        spell.setSchool((String)attributes.get(YAML_SCHOOL));
        spell.setReference((String)attributes.get(YAML_REFERENCE));
        spell.setSource((String)attributes.get(YAML_SOURCE));
        spell.setLevel((String)attributes.get(YAML_LEVEL));
        spell.setCastingTime((String)attributes.get(YAML_CASTING));
        spell.setComponents((String)attributes.get(YAML_COMPONENTS));
        spell.setRange((String)attributes.get(YAML_RANGE));
        spell.setTarget((String)attributes.get(YAML_TARGET));
        spell.setDuration((String)attributes.get(YAML_DURATION));
        spell.setSavingThrow((String)attributes.get(YAML_SAVING));
        spell.setSpellResistance((String)attributes.get(YAML_SPELL_RES));
        return spell.isValid() ? spell : null;
    }

    /**
     * Utility function that returns the template with regex replaced
     * @param template template (@see assets/templates.properties)
     * @param propKey name of the property
     * @param propValue value of the property
     * @return "" if value is null
     */
    private static String generateItemDetail(String template, String propKey, String propValue) {
        if(propValue != null) {
            return String.format(template, propKey, propValue);
        } else {
            return "";
        }
    }

    @Override
    public String generateDetails(@NonNull DBEntity entity, @NonNull String templateList, @NonNull String templateItem) {
        if(!(entity instanceof Spell)) {
            return "";
        }
        Spell spell = (Spell)entity;
        StringBuffer buf = new StringBuffer();
        String source = spell.getSource() == null ? null : getTranslatedText("source." + spell.getSource().toLowerCase());
        buf.append(generateItemDetail(templateItem, YAML_SOURCE, source));
        buf.append(generateItemDetail(templateItem, YAML_SCHOOL, spell.getSchool()));
        buf.append(generateItemDetail(templateItem, YAML_LEVEL, spell.getLevel()));
        buf.append(generateItemDetail(templateItem, YAML_CASTING, spell.getCastingTime()));
        buf.append(generateItemDetail(templateItem, YAML_COMPONENTS, spell.getComponents()));
        buf.append(generateItemDetail(templateItem, YAML_RANGE, spell.getRange()));
        buf.append(generateItemDetail(templateItem, YAML_TARGET, spell.getTarget()));
        buf.append(generateItemDetail(templateItem, YAML_DURATION, spell.getDuration()));
        buf.append(generateItemDetail(templateItem, YAML_SAVING, spell.getSavingThrow()));
        buf.append(generateItemDetail(templateItem, YAML_SPELL_RES, spell.getSpellResistance()));
        return String.format(templateList,buf.toString());
    }

}
