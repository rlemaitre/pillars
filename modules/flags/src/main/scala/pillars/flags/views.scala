// Copyright (c) 2024-2024 by Raphaël Lemaitre and Contributors
// This software is licensed under the Eclipse Public License v2.0 (EPL-2.0).
// For more information see LICENSE or https://opensource.org/license/epl-2-0

package pillars.flags

import io.circe.Codec
import sttp.tapir.Schema

final case class FlagDetails(status: Status) derives Codec.AsObject, Schema
