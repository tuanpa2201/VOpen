package base.util;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Textbox;

public class MQTTClient extends SelectorComposer<Component> implements EventListener<Event> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4242737824818144858L;
	
	public static String SERVER_URI = ConfigUtil.getConfig("BROKER_URI", "tcp://125.212.226.54:1923");
	
	@Wire
	Textbox txtClient;
	@Wire
	Textbox txtTopic;
	@Wire
	Intbox txtQos;
	@Wire
	Textbox txtMessage;
	@Wire
	Button btPublish;
	@Wire
	Checkbox cbRetain;
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		btPublish.addEventListener(Events.ON_CLICK, this);
	}
	@Override
	public void onEvent(Event event) throws Exception {
		MqttConnectOptions connOpts = new MqttConnectOptions();
		connOpts.setCleanSession(true);
		try {
			MqttClient client = new MqttClient(SERVER_URI, txtClient.getText());
			client.connect(connOpts);
			client.publish(txtTopic.getText(), txtMessage.getText().getBytes(), txtQos.getValue(), cbRetain.isChecked());
			client.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
