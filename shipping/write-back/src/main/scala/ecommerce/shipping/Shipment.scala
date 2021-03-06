package ecommerce.shipping

import pl.newicom.dddd.actor.{Config, ConfigClass}
import pl.newicom.dddd.aggregate._
import pl.newicom.dddd.office.LocalOfficeId
import pl.newicom.dddd.office.LocalOfficeId.fromRemoteId

object Shipment extends AggregateRootSupport {

  sealed trait Shipping extends Behavior[Event, Shipping, Config]

  implicit case object Uninitialized extends Shipping with Uninitialized[Shipping] {

    def actions: Actions =
      handleCommand {
        case CreateShipment(shipmentId, orderId) =>
          if (initialized) {
            error(s"Shipment $shipmentId already exists")
          } else {
            ShipmentCreated(shipmentId, orderId)
          }
      }
      .handleEvent {
        case ShipmentCreated(_, _) => Active
      }
  }

  case object Active extends Shipping {

    def actions: Actions = noActions

  }

  implicit val officeId: LocalOfficeId[Shipment] = fromRemoteId[Shipment](ShippingOfficeId)

}

import ecommerce.shipping.Shipment._

class Shipment(val config: Config) extends AggregateRoot[Event, Shipping, Shipment] with ConfigClass[Config]