package com.tendersaucer.joe.level.script;

import com.badlogic.gdx.math.MathUtils;
import com.tendersaucer.joe.DAO;
import com.tendersaucer.joe.level.Level;
import com.tendersaucer.joe.level.entity.Entity;

/**
 * Created by Alex on 9/16/2016.
 */
public final class RotateEntityScript extends Script {

    private Float startAngle;
    private Float lastAngle;
    private Float degrees;
    private float totalDegrees;
    private final float angularSpeed;
    private final String targetId;

    public RotateEntityScript(ScriptDefinition definition) {
        super(definition);

        targetId = definition.getStringProperty("target");
        angularSpeed = definition.getFloatProperty("angular_speed");
        totalDegrees = definition.getFloatProperty("degrees");

        degrees = totalDegrees;
        lastAngle = null;
    }

    @Override
    public void init() {
        super.init();

        boolean isTutorial = DAO.getInstance().getBoolean(DAO.IS_NEW_KEY, true);
        if (!isTutorial && id.equals("tutorial_end_script")) {
            setActive(true);
        }
    }

    @Override
    protected void tick() {
        Entity target = Level.getInstance().getEntity(targetId);
        if (startAngle == null) {
            startAngle = target.getAngle();
            lastAngle = startAngle;
        }

        target.setAngularSpeed(angularSpeed);

        degrees -= Math.abs(MathUtils.radiansToDegrees * (lastAngle - target.getAngle()));
        if (degrees <= 0) {
            target.setAngularSpeed(0);
            target.setAngle(startAngle + ((angularSpeed > 0 ? 1 : -1) * MathUtils.degreesToRadians * totalDegrees));
            setDone();
        }

        lastAngle = target.getAngle();
    }
}
