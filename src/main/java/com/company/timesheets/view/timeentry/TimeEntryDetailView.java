package com.company.timesheets.view.timeentry;

import com.company.timesheets.entity.Task;
import com.company.timesheets.entity.TimeEntry;
import com.company.timesheets.entity.TimeEntryStatus;
import com.company.timesheets.entity.User;
import com.company.timesheets.view.main.MainView;
import com.company.timesheets.view.task.TaskLookupView;
import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.Route;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.flowui.DialogWindows;
import io.jmix.flowui.component.combobox.EntityComboBox;
import io.jmix.flowui.component.select.JmixSelect;
import io.jmix.flowui.component.textarea.JmixTextArea;
import io.jmix.flowui.component.valuepicker.EntityPicker;
import io.jmix.flowui.kit.action.ActionPerformedEvent;
import io.jmix.flowui.model.CollectionLoader;
import io.jmix.flowui.model.InstanceContainer;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

@Route(value = "time-entries/:id", layout = MainView.class)
@ViewController("ts_TimeEntry.detail")
@ViewDescriptor("time-entry-detail-view.xml")
@EditedEntityContainer("timeEntryDc")
@DialogMode(width = "30em")
public class TimeEntryDetailView extends StandardDetailView<TimeEntry> {

    @Autowired
    private CurrentAuthentication currentAuthentication;
    @ViewComponent
    private JmixTextArea rejectionReasonField;
    @ViewComponent
    private CollectionLoader<Task> tasksDl;
    @ViewComponent
    private EntityComboBox<Task> taskField;
    @Autowired
    private DialogWindows dialogWindows;
    @ViewComponent
    private EntityPicker<User> userField;
    @ViewComponent
    private JmixSelect<TimeEntryStatus> statusField;

    @Subscribe("userField.assignSelf")
    public void onUserFieldAssignSelf(final ActionPerformedEvent event) {
        final User user = (User) currentAuthentication.getUser();
        getEditedEntity().setUser(user);
    }

    @Subscribe(id = "timeEntryDc", target = Target.DATA_CONTAINER)
    public void onTimeEntryDcItemChange(final InstanceContainer.ItemChangeEvent<TimeEntry> event) {
        updateRejectionReasonField();
        loadTasks();
    }


    @Subscribe(id = "timeEntryDc", target = Target.DATA_CONTAINER)
    public void onTimeEntryDcItemPropertyChange(final InstanceContainer.ItemPropertyChangeEvent<TimeEntry> event) {
        if ("status".equals(event.getProperty())) {
            updateRejectionReasonField();
        }
        if ("user".equals(event.getProperty())) {
            taskField.setReadOnly(getEditedEntity().getUser() == null);
            loadTasks();
        }
    }

    private void updateRejectionReasonField() {
    rejectionReasonField.setVisible(TimeEntryStatus.REJECTED == getEditedEntity().getStatus());
    }

    @Subscribe
    public void onInitEntity(final InitEntityEvent<TimeEntry> event) {
        TimeEntry timeEntry = event.getEntity();

        if (timeEntry.getDate() == null) {
            timeEntry.setDate(LocalDate.now());
        }

        userField.setReadOnly(timeEntry.getUser() != null);
        taskField.setReadOnly(timeEntry.getUser() == null || timeEntry.getTask() != null);
    }

    private void loadTasks() {
        User user = getEditedEntity().getUser();
        tasksDl.setParameter("username", user != null ? user.getUsername() : null);
        tasksDl.load();
    }

    @Subscribe("taskField.entityLookup")
    public void onTaskFieldEntityLookup(final ActionPerformedEvent event) {
        DialogWindow<TaskLookupView> dialogWindow = dialogWindows.lookup(taskField)
                .withViewClass(TaskLookupView.class)
                .build();
        dialogWindow.getView().setUser(getEditedEntity().getUser());
        dialogWindow.open();
    }

    @Subscribe
    public void onReady(final ReadyEvent event) {
        updateStatusFieldIcon();
    }

    @Subscribe("statusField")
    public void onStatusFieldComponentValueChange(final AbstractField.ComponentValueChangeEvent<JmixSelect<TimeEntryStatus>, TimeEntryStatus> event) {
        updateStatusFieldIcon();
    }

    private void updateStatusFieldIcon() {

        TimeEntryStatus status = statusField.getValue();
        Icon icon = status == null ? null : switch (status) {
            case NEW -> {
                Icon newIcon = VaadinIcon.PLUS_CIRCLE_O.create();
                newIcon.setColor("var(--lumo-primary-color)");
                yield newIcon;
            }
            case APPROVED -> {
                Icon approvedIcon = VaadinIcon.CHECK_CIRCLE_O.create();
                approvedIcon.setColor("var(--lumo-success-color)");
                yield approvedIcon;
            }
            case REJECTED -> {
                Icon rejectedIcon = VaadinIcon.EXCLAMATION_CIRCLE_O.create();
                rejectedIcon.setColor("var(--lumo-error-color)");
                yield rejectedIcon;
            }
            case CLOSED -> VaadinIcon.CLOSE_CIRCLE_O.create();
        };

        statusField.setPrefixComponent(icon);

    }


}