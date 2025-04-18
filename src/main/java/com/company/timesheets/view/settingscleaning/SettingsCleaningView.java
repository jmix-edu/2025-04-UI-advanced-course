package com.company.timesheets.view.settingscleaning;


import com.company.timesheets.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.flowui.settings.UserSettingsCache;
import io.jmix.flowui.settings.UserSettingsService;
import io.jmix.flowui.util.RemoveOperation;
import io.jmix.flowui.view.*;
import io.jmix.flowuidata.entity.UserSettingsItem;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "settings-cleaning-view", layout = MainView.class)
@ViewController(id = "ts_SettingsCleaningView")
@ViewDescriptor(path = "settings-cleaning-view.xml")
public class SettingsCleaningView extends StandardView {

    @Autowired
    private UserSettingsCache userSettingsCache;

    @Install(to = "userSettingsItemsDataGrid.remove", subject = "afterActionPerformedHandler")
    private void userSettingsItemsDataGridRemoveAfterActionPerformedHandler(final RemoveOperation.AfterActionPerformedEvent<UserSettingsItem> afterActionPerformedEvent) {
        userSettingsCache.clear();
    }
}