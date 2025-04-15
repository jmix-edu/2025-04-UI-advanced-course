package com.company.timesheets.view.mytimeentrieslist;


import com.company.timesheets.app.TimeEntrySupport;
import com.company.timesheets.entity.TimeEntry;
import com.company.timesheets.view.main.MainView;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.DialogWindows;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "my-time-entries", layout = MainView.class)
@ViewController(id = "ts_TimeEntry.my")
@ViewDescriptor(path = "my-time-entries-list-view.xml")
public class MyTimeEntriesListView extends StandardView {

    @ViewComponent
    private DataGrid<TimeEntry> timeEntriesDataGrid;
    @Autowired
    private TimeEntrySupport timeEntrySupport;
    @Autowired
    private DialogWindows dialogWindows;
    @ViewComponent
    private CollectionContainer<TimeEntry> timeEntriesDc;
    @ViewComponent
    private GridMenuItem<Object> editIntercepted;

    @Subscribe("timeEntriesDataGrid.copy")
    public void onTimeEntriesDataGridCopy(final ActionPerformedEvent event) {
        TimeEntry selected = timeEntriesDataGrid.getSingleSelectedItem();
        if (selected == null) {
            return;
        }

        TimeEntry copied = timeEntrySupport.copy(selected);

        dialogWindows.detail(timeEntriesDataGrid)
                .newEntity(copied)
                .open();
    }

    @Install(to = "contextMenu", subject = "dynamicContentHandler")
    private boolean contextMenuDynamicContentHandler(final TimeEntry timeEntry) {
       if (timeEntry == null) {
           return false;
       }

       timeEntriesDataGrid.select(timeEntry);

        return true;
    }
}