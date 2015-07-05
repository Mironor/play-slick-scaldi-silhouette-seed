package helpers

import com.mohiva.play.silhouette.api.util.{PasswordInfo, PasswordHasher}

class TestPasswordHasher extends PasswordHasher{
  override def id = "id"

  override def matches(passwordInfo: PasswordInfo, suppliedPassword: String) = passwordInfo.password == suppliedPassword

  override def hash(plainPassword: String) = PasswordInfo("", plainPassword)
}
