/*
 * Copyright (C) 2016 Baidu, Inc. All Rights Reserved.
 */
package alchemystar.lancelot.common.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import alchemystar.lancelot.route.util.StringUtil;
import groovy.lang.GroovyShell;

/**
 * TableRuleConfig
 *@Author lizhuyang
 */
public class TableRuleConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(TableRuleConfig.class);

    private final String name;
    private final List<String> columns;
    private final List<String> dbRuleArray;
    private final List<String> tbRuleArray;
    private final String tbPrefix;
    private Map<Integer, List<String>> tbMap;
    // key tbPrefix value=dataNodeIndex
    private Map<String, Integer> tbIndexMap;
    private boolean forceHit = false;
    private List<String> unlessSQL;

    @SuppressWarnings("unchecked")
    public TableRuleConfig(String name, String forceHit, String[] columns, List<String> dbRuleArray,
                           List<String> tbRuleArray, String tbPrefix, String[] unlessSQL) {
        if (name == null) {
            throw new IllegalArgumentException("name is null");
        }
        this.name = name;
        this.forceHit = BooleanUtils.toBoolean(forceHit);
        this.unlessSQL = Collections.unmodifiableList(Arrays.asList(unlessSQL));
        if (columns == null || columns.length == 0) {
            throw new IllegalArgumentException("no column is found!");
        }
        if (dbRuleArray == null || dbRuleArray.isEmpty()) {
            throw new IllegalArgumentException("dbRuleArray is empty!");
        }
        // if (tbRuleArray == null || tbRuleArray.isEmpty()) {
        // throw new IllegalArgumentException("tbRuleArray is empty!");
        // }

        this.columns = Collections.unmodifiableList(Arrays.asList(columns));
        this.dbRuleArray = dbRuleArray;
        this.tbRuleArray = tbRuleArray;

        this.tbPrefix = tbPrefix;
        try {
            if (StringUtil.isNotEmpty(tbPrefix)) {
                GroovyShell gs = new GroovyShell();
                tbMap = (Map<Integer, List<String>>) gs.evaluate(this.tbPrefix);

                if (tbMap.isEmpty()) {
                    throw new IllegalArgumentException("tbPrefix eval result is empty!");
                }
                tbIndexMap = new HashMap<String, Integer>();
                for (Map.Entry<Integer, List<String>> entry : tbMap.entrySet()) {
                    Integer dn = entry.getKey();
                    for (String tbPre : entry.getValue()) {
                        tbIndexMap.put(tbPre, dn);
                    }
                }
                LOGGER.info("加载[" + name + "]tbPrefix-->" + tbMap);
                LOGGER.info("加载[" + name + "]tbIndexMap-->" + tbIndexMap);

            }
        } catch (Exception e) {
            LOGGER.error("init table rule error!", e);
        }
    }

    /**
     * Getter method for property <tt>forceHit</tt>.
     * 
     * @return property value of forceHit
     */
    public boolean isForceHit() {
        return forceHit;
    }

    public boolean isTbShard() {
        return tbMap == null ? false : !tbMap.isEmpty();
    }

    /**
     * Getter method for property <tt>columns</tt>.
     * 
     * @return property value of columns
     */
    public List<String> getColumns() {
        return columns;
    }

    /**
     * Getter method for property <tt>dbRuleArray</tt>.
     * 
     * @return property value of dbRuleArray
     */
    public List<String> getDbRuleArray() {
        return dbRuleArray;
    }

    /**
     * Getter method for property <tt>tbRuleArray</tt>.
     * 
     * @return property value of tbRuleArray
     */
    public List<String> getTbRuleArray() {
        return tbRuleArray;
    }

    /**
     * Getter method for property <tt>tbMap</tt>.
     * 
     * @return property value of tbMap
     */
    public Map<Integer, List<String>> getTbMap() {
        return tbMap;
    }

    public Map<String, Integer> getTbIndexMap() {
        return tbIndexMap;
    }

    public String getName() {
        return name;
    }

    public List<String> getUnlessSQL() {
        return unlessSQL;
    }

}
