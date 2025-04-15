package com.company.timesheets.view.client;

import com.company.timesheets.entity.Client;
import com.company.timesheets.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;


@Route(value = "clients-simple", layout = MainView.class)
@ViewController(id = "ts_Client.simple_list")
@ViewDescriptor(path = "client-simple-list-view.xml")
@LookupComponent("clientsDataGrid")
@DialogMode(width = "64em")
public class ClientSimpleListView extends StandardListView<Client> {
}