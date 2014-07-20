package com.github.chenyoca.validation;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.github.chenyoca.validation.validators.ValidatorFactory;
import com.github.chenyoca.validation.supports.AbstractValidator;

/**
 * User: YooJia.Chen@gmail.com
 * Date: 2014-06-25
 * Android Validator
 */
public class FormValidator {

    final Context context;
    final MessageDisplay display;
    final View form;
    final SparseArray<_> configs = new SparseArray<_>();
    final SparseArray<_> weakHold = new SparseArray<_>();
    final SparseArray<String> values = new SparseArray<String>();

    public FormValidator(View form, MessageDisplay display){
        this.form = form;
        assert form != null;
        this.context = form.getContext();
        this.display = display;
    }

    public FormValidator(View form){
        this(form,new MessageDisplay() {
            @Override
            public void dismiss(EditText field) {
                field.setError(null);
            }

            @Override
            public void show(EditText field, String message) {
                field.setError(message);
            }
        });
    }

    /**
     * Add validate type to a view with view id.
     * @param viewId View ID
     * @param types Validate type
     * @return FormValidator instance.
     */
    public FormValidator add(int viewId, Type...types){
        for (Type t : types) add(viewId, t);
        return this;
    }

    /**
     * Add validate runners to a view with view id.
     * @param viewId View ID
     * @param validators Test validators
     * @return FormValidator instance.
     */
    public FormValidator add(int viewId, AbstractValidator...validators){
        if (validators == null || validators.length == 0){
            throw new IllegalArgumentException("Required 1 or more runner !");
        }
        _ item = configs.get(viewId);
        if (item != null){
            for (AbstractValidator v: validators) item.add(v);
        }else{
            item = create(viewId, validators[0]);
            for (int i=1;i<validators.length;i++) item.add(validators[i]);
        }
        return this;
    }

    private void add(int viewId, Type type){
        // If config(key by view id) exists, just add.
        _ item = configs.get(viewId);
        if (item != null){
            item.add(context, type);
            return;
        }
        // NO, create it.
        create(viewId, ValidatorFactory.build(context, type));
    }

    private _ create(int viewId, AbstractValidator validator){
        View field = form.findViewById(viewId);
        if ( ! (field instanceof EditText)){
            throw new IllegalArgumentException(
                    String.format("View(id=%d) IS NOT A EditText View !", viewId));
        }
        EditText editText = (EditText)field;
        _ item = new _(display, editText , validator);
        configs.put(viewId, item);
        weakHold.put(viewId, item);
        values.put(viewId,"");
        return item;
    }

    public FormValidator applyInputType(int...excludeViewIDs){
        for (int exclude : excludeViewIDs){
            weakHold.remove(exclude);
        }
        int size = weakHold.size();
        for (int i=0;i<size;i++) weakHold.valueAt(i).performInputType();
        return this;
    }

    public TestResult test(){
        return test(true);
    }

    public TestResult test(boolean continuousTest){
        boolean passFlag = true;
        String failedMsg = "NO_TEST_CONFIGURATIONS";
        String failedVal = null;
        TestResult r = null;
        int size = configs.size();
        for (int i=0;i<size;i++) {
            r = configs.valueAt(i).performTest();
            if (debug) Log.i("Test","Field tested: "+r);
            passFlag &= r.passed;
            failedMsg = passFlag ? null : r.message;
            failedVal = r.value;
            values.setValueAt(i, r.value);
            if (!passFlag && !continuousTest) break;
        }
        return new TestResult(r != null && passFlag,failedMsg,failedVal);
    }

    /**
     * Get an extra value from field that WITHOUT test config by view id.
     * @param viewId View id WITHOUT test config
     * @return String value
     */
    public String getExtraValue(int viewId){
        return ((TextView)form.findViewById(viewId)).getText().toString();
    }

    /**
     * Get value from test form.
     * @param viewId View id
     * @return String value
     */
    public String getValue(int viewId){
        return values.get(viewId);
    }

    boolean debug = false;
    public void debug(boolean enable){
        debug = enable;
    }
}
