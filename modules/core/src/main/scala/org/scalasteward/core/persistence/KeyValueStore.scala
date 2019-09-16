/*
 * Copyright 2018-2019 Scala Steward contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.scalasteward.core.persistence

import cats.Applicative
import cats.implicits._

trait KeyValueStore[F[_], K, V] {
  def get(key: K): F[Option[V]]

  def getMany(keys: List[K]): F[Map[K, V]]

  def modifyF(key: K)(f: Option[V] => F[Option[V]]): F[Option[V]]

  final def delete(key: K)(implicit F: Applicative[F]): F[Unit] =
    modify(key)(_ => None).void

  final def modify(key: K)(f: Option[V] => Option[V])(implicit F: Applicative[F]): F[Option[V]] =
    modifyF(key)(f.andThen(F.pure))

  final def put(key: K, value: V)(implicit F: Applicative[F]): F[Unit] =
    modify(key)(_ => Some(value)).void
}
