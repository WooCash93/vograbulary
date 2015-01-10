package com.github.donkirkby.vograbulary.client;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasWidgets;

public class AppController implements ValueChangeHandler<String> {
    private HasWidgets container;
    
    public AppController() {
        History.addValueChangeHandler(this);
    }

    @Override
    public void onValueChange(ValueChangeEvent<String> event) {
        this.container.clear();
        Composite presenter;
        if (event.getValue().equals(RussianDollsPresenter.HISTORY_TOKEN)) {
            presenter = new RussianDollsPresenter();
        }
        else if (event.getValue().equals(UltraghostPresenter.HISTORY_TOKEN)) {
            presenter = new UltraghostPresenter();
        }
        else {
            presenter = new MainPresenter();
        }
        this.container.add(presenter);
    }

    public void go(HasWidgets container) {
        this.container = container;
        History.newItem(MainPresenter.HISTORY_TOKEN);
    }
}