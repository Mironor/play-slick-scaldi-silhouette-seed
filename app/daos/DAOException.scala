package daos

case class DAOException(message: String) extends Exception(message)
