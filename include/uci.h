#pragma once

#include "position.h"
#include <string>

void parsePositionCommand(const std::string &line, Position &pos);
void uciLoop();